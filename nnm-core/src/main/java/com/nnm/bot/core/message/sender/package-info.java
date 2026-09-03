/**
 * 平台消息发送端口与路由。
 *
 * <p>Sender 负责执行具体发送，Router 只按平台选择 Sender；
 * 输出模式组合、渲染和能力降级属于 plugin/response。</p>
 */
package com.nnm.bot.core.message.sender;
