package com.nnm.bot.warframe.persistent.official.pojo.dto.maintenance;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialRecordSaveDeserializer;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;

/** 官方自维护请求，业务内容由 domain 确定具体字段合同。 */
@JsonDeserialize(using = OfficialRecordSaveDeserializer.class)
public record WarframeOfficialRecordSaveDTO(String domain, String bkey, OfficialRecordDTO data,
        boolean confirmed, boolean replaceExisting) { }
