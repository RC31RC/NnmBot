package com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot;

import java.util.List;

/** 记录级差异由服务端固定快照生成，审批不接受客户端提交业务 JSON。 */
public record WarframeOfficialRecordChangeVO(
        String path, String recordKey, String displayName, String changeType,
        String previousJson, String incomingJson,
        String previousLocalizedJson, String incomingLocalizedJson,
        List<WarframeOfficialFieldChangeVO> fieldChanges) {
}
