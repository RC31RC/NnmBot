package com.nnm.bot.core.channel.notification;

import com.nnm.bot.core.constant.NotificationConstants.GroupResultStatus;

import com.nnm.bot.core.plugin.model.DeliveryReport;

public record GroupNotificationResult(GroupResultStatus status, String message, DeliveryReport report) {

}
