package com.nnm.bot.warframe.messaging.model;

/**
 * Warframe 共享世界状态已更新事件。
 *
 * @param contentHash 共享快照内容哈希，用于日志和后续诊断
 */
public record WarframeWorldStateAvailableEvent(String contentHash) {
}
