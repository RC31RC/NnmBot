package com.nnm.bot.warframe.persistent.curated.parser.reference;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import jakarta.annotation.PostConstruct;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.curated.config.reference.WarframeCommunityProperties;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityInputDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityImportDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityImportIssueDTO;
import java.util.Map;
import java.util.Arrays;

/** 社区导入只处理对象、数组和稳定键字典容器，记录字段由 Jackson 自动绑定。 */
@Component
public class WarframeCommunityImportParser {
    /** 具体 DTO 的字段绑定器。 */
    @Resource
    private ObjectMapper mapper;
    /** 导入容量与记录上限。 */
    @Resource
    private WarframeCommunityProperties properties;

    /** 只用于固定社区字段的严格绑定器，不改变全局 HTTP 序列化设置。 */
    private ObjectMapper inputMapper;

    /** 初始化一次字段类型规则，避免每条记录复制绑定器。 */
    @PostConstruct
    void initialize() {
        inputMapper = mapper.copy().disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);
        for (CoercionInputShape shape : new CoercionInputShape[] {CoercionInputShape.Integer, CoercionInputShape.Float,
            CoercionInputShape.Boolean}) {
            inputMapper.coercionConfigFor(LogicalType.Textual).setCoercion(shape, CoercionAction.Fail);
        }
    }

    /**
     * 输入文档的容器结构不改变社区记录的固定字段合同。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param json 待绑定的 JSON 文本。
     * @return 有效记录、有界错误明细及完整的输入和无效记录数量。
     */
    public WarframeCommunityImportDTO parse(WarframeCommunityDataset dataset, String json) {
        try (JsonParser parser = mapper.getFactory().createParser(json)) {
            parser.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
            parser.nextToken();
            ImportState rows = new ImportState();
            readContainer(dataset, parser, rows);
            check(parser.nextToken() == null, "输入包含多余的尾随 JSON");
            return new WarframeCommunityImportDTO(Collections.unmodifiableMap(rows.records), rows.inputCount,
                rows.invalidCount, List.copyOf(rows.issues));
        } catch (IOException exception) {
            throw new BusinessException(WarframeManagementErrorCode.COMMUNITY_INVALID_INPUT,
                "社区 JSON 与固定字段合同不符：" + exception.getMessage(), exception);
        }
    }

    /**
     * 流式区分包裹对象、数组、单条记录和稳定键字典。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param parser 已定位到当前输入值的流式解析器。
     * @param rows 当前待处理的数据行。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private void readContainer(WarframeCommunityDataset dataset, JsonParser parser, ImportState rows)
        throws IOException {
        if (parser.currentToken() == JsonToken.START_ARRAY) {
            while (parser.nextToken() != JsonToken.END_ARRAY)
                readRow(dataset, parser, null, rows);
            return;
        }
        check(parser.currentToken() == JsonToken.START_OBJECT, "输入必须为业务对象、数组或稳定键字典");
        try (TokenBuffer document = new TokenBuffer(parser)) {
            document.copyCurrentStructure(parser);
            try (JsonParser input = document.asParserOnFirstToken()) {
                JsonToken next = input.nextToken();
                if (next == JsonToken.END_OBJECT)
                    return;
                String first = input.currentName();
                if (dataset.fields().contains(first) || "key".equals(first)) {
                    try (JsonParser row = document.asParserOnFirstToken()) {
                        readRow(dataset, row, null, rows);
                    }
                } else if ("records".equals(first)) {
                    input.nextToken();
                    readContainer(dataset, input, rows);
                    check(input.nextToken() == JsonToken.END_OBJECT, "records 包装对象不能混入其他字段");
                } else {
                    do {
                        check(input.currentToken() == JsonToken.FIELD_NAME, "字典成员必须具有稳定键");
                        String key = input.currentName();
                        input.nextToken();
                        readRow(dataset, input, key, rows);
                    } while (input.nextToken() != JsonToken.END_OBJECT);
                }
            }
        }
    }

    /**
     * 除可选记录键外，所有业务字段由明确 DTO 绑定并通过具体访问器校验。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param parser 已定位到当前输入值的流式解析器。
     * @param dictionaryKey 字典键。
     * @param rows 当前待处理的数据行。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private void readRow(WarframeCommunityDataset dataset, JsonParser parser, String dictionaryKey, ImportState rows)
        throws IOException {
        rows.inputCount++;
        BusinessAssert.isTrue(rows.inputCount <= properties.getMaxRecords(),
            WarframeManagementErrorCode.IMPORT_FILE_TOO_LARGE);
        if (parser.currentToken() != JsonToken.START_OBJECT) {
            parser.skipChildren();
            addIssue(rows, dictionaryKey, null, "社区记录必须为对象");
            return;
        }
        try (TokenBuffer buffer = new TokenBuffer(parser)) {
            // 先完整消费一条记录；语法损坏在此直接失败，字段错误才允许继续读取下一条。
            buffer.copyCurrentStructure(parser);
            try {
                bindRow(dataset, buffer, dictionaryKey, rows);
            } catch (JsonMappingException exception) {
                String field = exception
                    .getPath().stream().map(reference -> reference.getFieldName() == null
                        ? Integer.toString(reference.getIndex()) : reference.getFieldName())
                    .collect(Collectors.joining("."));
                addIssue(rows, dictionaryKey, field, "字段类型或名称无效：" + field);
            } catch (BusinessException exception) {
                addIssue(rows, dictionaryKey, null, exception.getMessage());
            }
        }
    }

    /**
     * 绑定并校验一条已完整读取的记录。
     *
     * @param dataset 人工资料类型。
     * @param buffer 当前记录的独立输入缓冲。
     * @param dictionaryKey 字典容器给出的稳定业务键。
     * @param rows 本次调用私有的解析状态。
     * @throws IOException 字段绑定或缓冲读取失败时抛出。
     */
    private void bindRow(WarframeCommunityDataset dataset, TokenBuffer buffer, String dictionaryKey, ImportState rows)
        throws IOException {
        String explicitKey = null;
        try (JsonParser keys = buffer.asParserOnFirstToken()) {
            while (keys.nextToken() != JsonToken.END_OBJECT) {
                String field = keys.currentName();
                keys.nextToken();
                if ("key".equals(field)) {
                    check(keys.currentToken() == JsonToken.VALUE_STRING, "key 必须是字符串");
                    explicitKey = keys.getText();
                }
                keys.skipChildren();
            }
        }
        Class<? extends WarframeCommunityInputDTO> type = switch (dataset) {
            case ALIAS -> WarframeCommunityRecordDTO.AliasDTO.class;
            case TERM -> WarframeCommunityRecordDTO.TermDTO.class;
            case ARBITRATION_ESSENCE -> WarframeCommunityRecordDTO.EssenceDTO.class;
            case RIVEN_ALIAS -> WarframeCommunityRecordDTO.RivenAliasDTO.class;
        };
        WarframeCommunityInputDTO value;
        try (JsonParser input = buffer.asParserOnFirstToken()) {
            value = inputMapper.readerFor(type).with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .without(DeserializationFeature.ACCEPT_FLOAT_AS_INT).readValue(input);
        }
        String naturalKey = switch (value) {
            case WarframeCommunityRecordDTO.AliasDTO item -> {
                text(item.aliasName(), "aliasName", false);
                text(item.targetName(), "targetName", false);
                yield item.aliasName();
            }
            case WarframeCommunityRecordDTO.TermDTO item -> {
                text(item.englishText(), "englishText", false);
                text(item.chineseText(), "chineseText", false);
                String group = item.termGroup() == null ? "" : item.termGroup();
                text(group, "termGroup", true);
                value = new WarframeCommunityRecordDTO.TermDTO(group, item.englishText(), item.chineseText());
                yield group + ":" + item.englishText();
            }
            case WarframeCommunityRecordDTO.EssenceDTO item -> {
                text(item.nodeName(), "nodeName", false);
                check(item.essenceAmount() != null && item.essenceAmount() >= 0, "essenceAmount 必须为非负整数");
                yield item.nodeName();
            }
            case WarframeCommunityRecordDTO.RivenAliasDTO item -> {
                text(item.attributeCode(), "attributeCode", false);
                text(item.aliasName(), "aliasName", false);
                check(item.attributeCode().matches("[a-z0-9_/]+(?:\\+[a-z0-9_/]+){0,2}"), "紫卡别名必须关联一至三个官方属性代码");
                String[] codes = item.attributeCode().split("[+]");
                check(Arrays.stream(codes).distinct().count() == codes.length, "组合不能重复选择同一个官方属性");
                Arrays.sort(codes);
                value = new WarframeCommunityRecordDTO.RivenAliasDTO(String.join("+", codes), item.aliasName());
                yield item.aliasName();
            }
        };
        String key = explicitKey != null ? explicitKey : dictionaryKey != null ? dictionaryKey : naturalKey;
        check(StrUtil.isNotBlank(key) && key.length() <= 500, "业务键必须非空且不超过 500 字符");
        check(!rows.records.containsKey(key), "输入包含重复业务键：" + key);
        rows.records.put(key, value);
    }

    /**
     * 统计无效记录并在配置容量内保留错误，不让错误报告随文件无限增长。
     *
     * @param rows 本次解析状态。
     * @param key 已知的字典业务键，其他容器为 null。
     * @param field Jackson 给出的字段路径，业务约束错误可为空。
     * @param message 可向维护人员展示的修正原因。
     */
    private void addIssue(ImportState rows, String key, String field, String message) {
        rows.invalidCount++;
        if (rows.issues.size() < properties.getMaxValidationIssues())
            rows.issues.add(new WarframeCommunityImportIssueDTO(rows.inputCount, key, field, message));
    }

    /** 只属于一次解析调用，禁止跨导入复用可变状态。 */
    private static final class ImportState {
        private final Map<String, WarframeCommunityInputDTO> records = new LinkedHashMap<>();
        private final List<WarframeCommunityImportIssueDTO> issues = new ArrayList<>();
        private int inputCount;
        private int invalidCount;
    }

    /**
     * 校验固定的文本字段，不容许 Jackson 宽松转换掩盖空值。
     *
     * @param value 待校验或规范化的文本。
     * @param field 已经声明的字段或字段标识。
     * @param blankAllowed 空白允许。
     */
    private static void text(String value, String field, boolean blankAllowed) {
        check(value != null && value.length() <= 10_000 && (blankAllowed || StrUtil.isNotBlank(value)),
            "字段无效：" + field);
    }

    /**
     * 输入合同错误使用稳定的社区导入错误码。
     *
     * @param condition 条件。
     * @param message 待发送或处理的消息。
     */
    private static void check(boolean condition, String message) {
        BusinessAssert.isTrue(condition, WarframeManagementErrorCode.COMMUNITY_INVALID_INPUT, message);
    }
}
