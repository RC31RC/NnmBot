package com.nnm.bot.admin.dto.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuSaveRequest(
        Long parentId,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 255) String path,
        @Size(max = 255) String component,
        @NotBlank @Size(max = 100) String title,
        @Size(max = 100) String icon,
        int sortOrder,
        @NotNull Boolean keepAlive,
        @NotNull Boolean visible,
        @NotNull Boolean enabled
) {
}
