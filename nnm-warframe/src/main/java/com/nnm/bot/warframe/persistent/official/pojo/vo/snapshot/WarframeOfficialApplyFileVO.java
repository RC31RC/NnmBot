package com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot;

/** 管理端逐文件入库进度；各类别完成标记仅在数据库事务提交后为真。 */
public record WarframeOfficialApplyFileVO(String path, String status, boolean addedDone, boolean changedDone,
    boolean removedDone, int attempts, String error) {
}
