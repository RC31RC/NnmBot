package com.nnm.bot.core.channel.notification;

import com.nnm.bot.core.channel.notification.constant.NotificationConstants.GroupResultStatus;

import com.nnm.bot.core.plugin.response.DeliveryReport;

/** 一次群通知的终态、摘要与逐条投递报告。 */
public record GroupNotificationResult(GroupResultStatus status, String message, DeliveryReport report) {

}
