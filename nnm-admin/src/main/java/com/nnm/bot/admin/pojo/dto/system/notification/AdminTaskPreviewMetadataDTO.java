package com.nnm.bot.admin.pojo.dto.system.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 异构任务结果中通知真正需要的预览标识及有效期。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AdminTaskPreviewMetadataDTO(String id, String dataset, String expiresAt) {
}
