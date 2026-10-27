package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

/** manifest 中单个官方文件的摘要及记录数。 */
public record OfficialFileDigestDTO(String path, String sha256, Long recordCount) {
}
