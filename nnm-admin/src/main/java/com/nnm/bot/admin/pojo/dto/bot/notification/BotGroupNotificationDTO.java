package com.nnm.bot.admin.pojo.dto.bot.notification;

import com.nnm.bot.core.mq.message.notification.NotificationContent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.time.Instant;

public record BotGroupNotificationDTO(
        @NotNull
        UUID submissionId,
        @NotEmpty
        @Size(max = 200)
        List<
                @NotNull
                @Positive
                Long> groupIds,
        @NotNull
        NotificationContent content,
        Instant scheduledAt) {
    public BotGroupNotificationDTO {
        if (groupIds != null) groupIds = Collections.unmodifiableList(new ArrayList<>(groupIds));
    }
}
