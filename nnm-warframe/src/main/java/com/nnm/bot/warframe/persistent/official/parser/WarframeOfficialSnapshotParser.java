package com.nnm.bot.warframe.persistent.official.parser;

import java.util.Comparator;
import java.nio.file.Files;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFileDigestDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialImportRecordDTO;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.METADATA_BKEY;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest.FileEntry;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonParser;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputTypes;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialObjectFields;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialRegionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialEnemyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialRivenWeaponDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialNightwaveDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyStageDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialDropTableDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialRewardPoolDTO;
import java.util.function.Consumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialInputParser;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 消费固定版本业务成品；嵌套集合按发布键分域，数组位置不构成记录身份。 */
@Component
public class WarframeOfficialSnapshotParser {
    public static final int CONTRACT_VERSION = 9;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 列出当前官方文件适配器已经登记的业务领域代码。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public List<String> supportedDomainCodes() {
        return domains().stream().map(WarframeOfficialDataDomain::domainCode).sorted().toList();
    }

    /**
     * 列出当前官方文件适配器支持的完整相对路径。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public List<String> supportedPaths() {
        return WarframeOfficialDataDomain.paths().stream().sorted().toList();
    }

    /**
     * 核对 manifest 中声明的文件，并构造待处理文件列表。
     *
     * @param directory 本次读取或存储使用的目录。
     * @param manifest 发布清单。
     * @return 按当前条件筛选或转换后的数据集合。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    public List<OfficialFileDigestDTO> validateSnapshotFiles(Path directory, WarframeOfficialManifest manifest) throws IOException {
        List<OfficialFileDigestDTO> files = parseFileDigests(manifest);
        for (OfficialFileDigestDTO file : files) {
            check(Files.isRegularFile(directory.resolve(file.path())),
                    "成品 JSON 不存在：" + file.path());
        }
        return files;
    }

    /**
     * 将 manifest 文件条目转换为当前审核需要的文件状态。
     *
     * @param manifest 发布清单。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public List<OfficialFileDigestDTO> parseFileDigests(WarframeOfficialManifest manifest) {
        Map<String,FileEntry> entries = new LinkedHashMap<>();
        for (FileEntry entry : manifest.files()) {
            check(WarframeOfficialDataDomain.fromPath(entry.path()) != null,
                    "当前 NnmBot 版本没有该 JSON 文件的实体映射，拒绝部分采纳：" + entry.path());
            check(entries.putIfAbsent(entry.path(), entry) == null,
                    "manifest 包含重复成品路径：" + entry.path());
        }
        check(entries.keySet().equals(Set.copyOf(supportedPaths())),
                "manifest 文件集合与 NnmBot 实体契约不一致，拒绝部分采纳");

        List<OfficialFileDigestDTO> payloads = new ArrayList<>();
        for (WarframeOfficialDataDomain type : domains()) {
            FileEntry entry = entries.get(type.productPath());
            payloads.add(new OfficialFileDigestDTO(entry.path(), entry.sha256(), entry.recordCount()));
        }
        return List.copyOf(payloads);
    }

    /**
     * 取得或更新当前业务使用的数据来源。
     *
     * @param directory 本次读取或存储使用的目录。
     * @param paths 待处理文件的路径集合。
     * @return 游戏资料数据文件记录来源对象。
     */
    public WarframeOfficialRecordSource recordSource(Path directory, Set<String> paths) {
        return consumer -> forEachRecord(directory, paths, consumer);
    }

    /**
     * 读取输入并生成类型化数据。
     *
     * @param directory 本次读取或存储使用的目录。
     * @param paths 待处理文件的路径集合。
     * @return 按当前条件筛选或转换后的数据集合。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    public List<OfficialImportRecordDTO> readRecords(Path directory, Set<String> paths) throws IOException {
        List<OfficialImportRecordDTO> result = new ArrayList<>();
        forEachRecord(directory, paths, result::add);
        return result;
    }

    /**
     * 按文件真实容器读取，每条记录直接绑定该文件的具体 DTO。
     *
     * @param directory 本次读取或存储使用的目录。
     * @param paths 待处理文件的路径集合。
     * @param consumer 逐条接收处理结果的回调。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    public void forEachRecord(Path directory, Set<String> paths, Consumer<OfficialImportRecordDTO> consumer) throws IOException {
        RecordSink records = new RecordSink(consumer);
        for (WarframeOfficialDataDomain domain : domains()) {
            if (!paths.contains(domain.productPath())) continue;
            try (JsonParser parser = objectMapper.getFactory().createParser(directory.resolve(domain.productPath()).toFile())) {
                JsonToken token = parser.nextToken();
                if (domain.name().startsWith("USAGE_")) {
                    array(parser, records, domain, "");
                } else if (Set.of(WarframeOfficialDataDomain.REGION, WarframeOfficialDataDomain.RELIC,
                        WarframeOfficialDataDomain.RIVEN_WEAPON, WarframeOfficialDataDomain.NIGHTWAVE,
                        WarframeOfficialDataDomain.BOUNTY, WarframeOfficialDataDomain.DROP_TABLE,
                        WarframeOfficialDataDomain.ACTIVITY).contains(domain)) {
                    wrapped(parser, records, domain);
                } else if (Set.of(WarframeOfficialDataDomain.DOJO_RECIPE, WarframeOfficialDataDomain.ENEMY,
                        WarframeOfficialDataDomain.CODEX).contains(domain)) {
                    check(token == JsonToken.START_OBJECT, "分组文件必须为对象：" + domain.productPath());
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        String group = parser.currentName();
                        parser.nextToken();
                        dictionary(parser, records, domain, group);
                    }
                } else if (token == JsonToken.START_ARRAY) {
                    array(parser, records, domain, "");
                } else {
                    dictionary(parser, records, domain, "");
                }
                check(parser.nextToken() == null, "成品文件包含尾随 JSON：" + domain.productPath());
            }
        }
    }

    /**
     * 读取具有明确业务分区的文件；其余包头字段绑定同文件的具体元数据字段。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param records 待批量处理的业务记录。
     * @param domain 官方数据文件所属的业务领域。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private void wrapped(JsonParser parser, RecordSink records, WarframeOfficialDataDomain domain) throws IOException {
        check(parser.currentToken() == JsonToken.START_OBJECT, "包装文件必须为对象：" + domain.productPath());
        OfficialObjectDTO metadata = newInput(domain);
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            check(parser.currentToken() == JsonToken.FIELD_NAME, "文件字段结构无效：" + domain.productPath());
            String field = parser.currentName();
            parser.nextToken();
            switch (domain) {
                case REGION -> {
                    if ("systems".equals(field)) systems(parser, records, domain, (OfficialRegionDTO) metadata);
                    else OfficialObjectFields.read(objectMapper, parser, metadata, field);
                }
                case RELIC -> {
                    if ("relics".equals(field)) array(parser, records, domain, "");
                    else OfficialObjectFields.read(objectMapper, parser, metadata, field);
                }
                case RIVEN_WEAPON -> {
                    if ("weapons".equals(field)) array(parser, records, domain, "");
                    else OfficialObjectFields.read(objectMapper, parser, metadata, field);
                }
                case NIGHTWAVE -> {
                    if ("challenges".equals(field)) dictionary(parser, records, domain, "challenges");
                    else if ("rewards".equals(field)) nightwaveRewards(parser, records, domain);
                    else OfficialObjectFields.read(objectMapper, parser, metadata, field);
                }
                case BOUNTY -> {
                    if ("definitions".equals(field) || "rewardPools".equals(field)) dictionary(parser, records, domain, field);
                    else OfficialObjectFields.read(objectMapper, parser, metadata, field);
                }
                case DROP_TABLE -> {
                    if ("contexts".equals(field) || "regionResources".equals(field)) array(parser, records, domain, field);
                    else OfficialObjectFields.read(objectMapper, parser, metadata, field);
                }
                case ACTIVITY -> {
                    if ("conquests__missionVariants".equals(field)) {
                        while (parser.nextToken() != JsonToken.END_OBJECT) {
                            String variant = parser.currentName(); parser.nextToken();
                            dictionary(parser, records, domain, "conquests/missionVariants/" + variant);
                        }
                    } else if (field.startsWith("conquests__") || field.startsWith("descents__")) {
                        dictionary(parser, records, domain, field.replace("__", "/"));
                    } else OfficialObjectFields.read(objectMapper, parser, metadata, field);
                }
                default -> throw new IllegalArgumentException("未定义包装文件：" + domain);
            }
        }
        if (domain != WarframeOfficialDataDomain.ACTIVITY) add(records, domain, METADATA_BKEY, (OfficialRecordDTO) metadata);
    }

    /**
     * 星图节点逐条导入，星系单值元数据仍保留在该 JSON 的元数据记录。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param records 待批量处理的业务记录。
     * @param domain 官方数据文件所属的业务领域。
     * @param metadata 元数据。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private void systems(JsonParser parser, RecordSink records, WarframeOfficialDataDomain domain, OfficialRegionDTO metadata)
            throws IOException {
        check(parser.currentToken() == JsonToken.START_ARRAY, "systems 必须是数组");
        List<OfficialRegionDTO.SystemsDTO> systems = new ArrayList<>();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            OfficialRegionDTO.SystemsDTO system = new OfficialRegionDTO.SystemsDTO();
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String field = parser.currentName(); parser.nextToken();
                if ("nodes".equals(field)) array(parser, records, domain, "");
                else OfficialObjectFields.read(objectMapper, parser, system, field);
            }
            systems.add(system);
        }
        metadata.setSystems(systems);
    }

    /**
     * 数组记录从具体字段获得稳定身份，不使用数组序号作为业务键。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param records 待批量处理的业务记录。
     * @param domain 官方数据文件所属的业务领域。
     * @param group 分组。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private void array(JsonParser parser, RecordSink records, WarframeOfficialDataDomain domain, String group)
            throws IOException {
        check(parser.currentToken() == JsonToken.START_ARRAY, "业务集合必须为数组：" + domain.productPath() + "/" + group);
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            check(parser.currentToken() == JsonToken.START_OBJECT, "业务记录必须为对象：" + domain.productPath());
            OfficialRecordDTO value = WarframeOfficialInputParser.parseCurrentValue(objectMapper, domain, parser);
            String key = value.stableKey();
            if (domain.name().startsWith("USAGE_")) key = usageKey(value);
            if (domain == WarframeOfficialDataDomain.DROP_TABLE && "regionResources".equals(group)) {
                key = ((OfficialDropTableDTO) value).getSystemIndex().toBigIntegerExact().toString();
            }
            if (domain == WarframeOfficialDataDomain.RIVEN_WEAPON) validateListings((OfficialRivenWeaponDTO) value);
            check(StrUtil.isNotBlank(key), "成品缺少明确的稳定业务键：" + domain.productPath());
            add(records, domain, StrUtil.isEmpty(group) ? key : group + "/" + key, value);
        }
    }

    /**
     * 使用统计的五个固定身份字段组成稳定组合键。
     *
     * @param value 待处理的字段值。
     * @return 使用统计键对应的文本。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private String usageKey(OfficialRecordDTO value) throws IOException {
        OfficialObjectDTO row = (OfficialObjectDTO) value;
        List<Object> keys = new ArrayList<>();
        for (String field : List.of("year", "platform", "category", "item", "rank")) {
            Object item = OfficialObjectFields.get(row, field);
            check(item != null, "统计缺少身份字段：" + field); keys.add(item);
        }
        return objectMapper.writeValueAsString(keys);
    }

    /**
     * 字典键只表达记录身份，成员值直接自动绑定具体模型。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param records 待批量处理的业务记录。
     * @param domain 官方数据文件所属的业务领域。
     * @param group 分组。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private void dictionary(JsonParser parser, RecordSink records, WarframeOfficialDataDomain domain, String group)
            throws IOException {
        check(parser.currentToken() == JsonToken.START_OBJECT, "业务集合必须为字典：" + domain.productPath() + "/" + group);
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            check(parser.currentToken() == JsonToken.FIELD_NAME, "字典成员缺少名称");
            String key = parser.currentName(); parser.nextToken();
            OfficialRecordDTO value;
            if (domain == WarframeOfficialDataDomain.BOUNTY && parser.currentToken() == JsonToken.START_ARRAY) {
                OfficialBountyDTO pool = new OfficialBountyDTO();
                pool.setPoolKey(key);
                pool.setStages(objectMapper.readerForListOf(OfficialBountyStageDTO.class).readValue(parser));
                value = pool;
            } else if (domain == WarframeOfficialDataDomain.ENEMY && "droptables".equals(group)) {
                OfficialEnemyDTO dropTable = new OfficialEnemyDTO();
                dropTable.setPoolKey(key);
                dropTable.setStages(objectMapper.readerForListOf(OfficialEnemyDTO.StagesDTO.class).readValue(parser));
                value = dropTable;
            } else if (domain == WarframeOfficialDataDomain.REWARD_POOL && parser.currentToken() == JsonToken.START_ARRAY) {
                OfficialRewardPoolDTO pool = new OfficialRewardPoolDTO();
                pool.setPoolKey(key);
                pool.setStages(objectMapper.readValue(parser, new TypeReference<List<List<OfficialRewardPoolDTO.StagesDTO>>>() {}));
                value = pool;
            } else {
                value = WarframeOfficialInputParser.parseCurrentValue(objectMapper, domain, parser);
            }
            if (StrUtil.isEmpty(group) && key.startsWith("/") && value.stableKey() != null) {
                check(key.equals(value.stableKey()), "字典键与显式业务键不一致：" + domain.productPath());
            }
            add(records, domain, StrUtil.isEmpty(group) ? key : group + "/" + key, value);
        }
    }

    /**
     * 夜波奖励以物品和数量去重，不依赖列表排序。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param records 待批量处理的业务记录。
     * @param domain 官方数据文件所属的业务领域。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private void nightwaveRewards(JsonParser parser, RecordSink records, WarframeOfficialDataDomain domain) throws IOException {
        check(parser.currentToken() == JsonToken.START_ARRAY, "夜波奖励必须为数组");
        Map<String, OfficialNightwaveDTO> rewards = new LinkedHashMap<>();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            OfficialNightwaveDTO reward = (OfficialNightwaveDTO) WarframeOfficialInputParser.parseCurrentValue(objectMapper, domain, parser);
            check(reward.stableKey() != null, "夜波奖励缺少物品业务键");
            String key = "rewards/" + reward.stableKey() + "/" + (reward.getItemCount() == null ? "unit" : reward.getItemCount().stripTrailingZeros().toPlainString());
            OfficialNightwaveDTO before = rewards.putIfAbsent(key, reward);
            check(before == null || objectMapper.writeValueAsString(before).equals(objectMapper.writeValueAsString(reward)),
                    "夜波奖励存在同键冲突：" + key);
        }
        for (var entry : rewards.entrySet()) add(records, domain, entry.getKey(), entry.getValue());
    }

    /**
     * 同一裂罅武器下商品 ID 必须存在且唯一。
     *
     * @param value 待处理的字段值。
     */
    private void validateListings(OfficialRivenWeaponDTO value) {
        check(value.getMarketListings() != null, "裂罅武器缺少 marketListings");
        Set<String> ids = new HashSet<>();
        for (var listing : value.getMarketListings()) {
            check(StrUtil.isNotBlank(listing.getId()) && ids.add(listing.getId()), "裂罅商品 ID 缺失或重复");
        }
    }

    /**
     * 创建该文件已登记的具体输入对象，不使用通用业务字段容器。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @return 符合当前官方文件固定字段合同的非受管详情。
     */
    private OfficialObjectDTO newInput(WarframeOfficialDataDomain domain) {
        try {
            return (OfficialObjectDTO) OfficialInputTypes.type(domain).getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("官方输入类型无法构造：" + domain, exception);
        }
    }

    /**
     * 由具体对象生成审核原文和检索摘要。
     *
     * @param records 待批量处理的业务记录。
     * @param domain 官方数据文件所属的业务领域。
     * @param key 当前记录或字段的查找键。
     * @param value 待处理的字段值。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private void add(RecordSink records, WarframeOfficialDataDomain domain, String key, OfficialRecordDTO value)
            throws IOException {
        check(StrUtil.isNotBlank(key) && key.length() <= 500, "成品业务键长度无效：" + domain.productPath());
        OfficialRecordHeaderDTO header = value.header();
        String json = objectMapper.writeValueAsString(value);
        OfficialImportRecordDTO record = new OfficialImportRecordDTO(domain.productPath(), domain, key, header.officialName(),
                header.localizedName(), header.parentName(), header.productCategory(), header.description(), json, json);
        check(records.add(record), "成品存在重复业务键：" + domain.productPath() + "/" + key);
    }

    /**
     * 按官方路径稳定排序，避免导入批次受枚举声明顺序影响。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<WarframeOfficialDataDomain> domains() {
        return Arrays.stream(WarframeOfficialDataDomain.values()).filter(domain -> domain != WarframeOfficialDataDomain.OTHER)
                .sorted(Comparator.comparing(WarframeOfficialDataDomain::productPath)).toList();
    }

    /**
     * 将输入契约错误统一转换为稳定业务错误。
     *
     * @param valid 有效。
     * @param message 待发送或处理的消息。
     */
    private static void check(boolean valid, String message) {
        BusinessAssert.isTrue(valid, WarframeManagementErrorCode.DATASET_INVALID, message);
    }

    /** 同一冻结输入内只允许一个明确业务身份。 */
    private static final class RecordSink {
        private final Set<String> identities = new HashSet<>();
        private final Consumer<OfficialImportRecordDTO> consumer;

        /**
         * 构造记录逐条接收器，并保存初始化参数。
         *
         * @param consumer 逐条接收处理结果的回调。
         */
        private RecordSink(Consumer<OfficialImportRecordDTO> consumer) { this.consumer = consumer; }

        /**
         * 加入一条数据并维护当前容器。
         *
         * @param record 当前业务记录。
         * @return 满足加入一条数据并维护当前容器时为真，否则为假。
         */
        private boolean add(OfficialImportRecordDTO record) {
            if (!identities.add(record.identity())) return false;
            consumer.accept(record); return true;
        }
    }

}
