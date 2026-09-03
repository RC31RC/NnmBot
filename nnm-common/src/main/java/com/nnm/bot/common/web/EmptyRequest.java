package com.nnm.bot.common.web;

/**
 * 不需要业务参数的请求体。
 *
 * <p>管理端查询统一使用 POST 时，用空对象保持请求格式一致。</p>
 */
public record EmptyRequest() {
}
