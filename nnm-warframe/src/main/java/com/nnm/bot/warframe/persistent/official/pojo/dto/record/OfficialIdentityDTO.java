package com.nnm.bot.warframe.persistent.official.pojo.dto.record;

/** 单个 JSON 内的导入身份索引；不加载名称、描述或源原文。 */
public interface OfficialIdentityDTO {
    /**
     * 读取本地记录主键。
     *
     * @return 本地记录主键。
     */
    Long getId();

    /**
     * 读取稳定业务键。
     *
     * @return 稳定业务键。
     */
    String getBkey();
}
