package com.nnm.bot.warframe.persistent.official.parser.input;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.maintenance.WarframeOfficialRecordSaveDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import java.io.IOException;

/** 允许 domain 出现在 data 前后，业务载荷只绑定指定文件的具体类型。 */
public class OfficialRecordSaveDeserializer extends JsonDeserializer<WarframeOfficialRecordSaveDTO> {
    /**
     * 仅缓存待判定类型的 token 序列，不生成任意字段树或 Map。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param context Jackson 当前反序列化上下文，用于报告协议字段错误。
     * @return 游戏资料官方记录保存对象。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    @Override
    public WarframeOfficialRecordSaveDTO deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String domain = null; String bkey = null; boolean confirmed = false; boolean replace = false;
        try (TokenBuffer data = new TokenBuffer(parser)) {
            boolean found = false;
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String field = parser.currentName(); parser.nextToken();
                switch (field) {
                    case "domain" -> domain = parser.getValueAsString();
                    case "bkey" -> bkey = parser.getValueAsString();
                    case "confirmed" -> confirmed = parser.getBooleanValue();
                    case "replaceExisting" -> replace = parser.getBooleanValue();
                    case "data" -> { data.copyCurrentStructure(parser); found = true; }
                    default -> { return context.reportInputMismatch(WarframeOfficialRecordSaveDTO.class, "未登记的请求字段：%s", field); }
                }
            }
            if (!found || domain == null) return context.reportInputMismatch(WarframeOfficialRecordSaveDTO.class, "必须提供 domain 与 data");
            OfficialRecordDTO value;
            try (JsonParser input = data.asParserOnFirstToken()) {
                value = WarframeOfficialInputParser.parseCurrentValue(
                        (ObjectMapper) parser.getCodec(), WarframeOfficialDataDomain.fromCode(domain), input);
            }
            return new WarframeOfficialRecordSaveDTO(domain, bkey, value, confirmed, replace);
        }
    }
}
