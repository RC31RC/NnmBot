package com.nnm.bot.admin.pojo.dto.bot.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

public record BotCommandGroupSaveDTO(
        Long id,
        @NotBlank
        @Size(max = 80)
        String code,
        @NotBlank
        @Size(max = 100)
        String name,
        boolean enabled,
        @NotNull
        Integer version,
        @NotNull
        @Size(min = 1, max = 32)
        List<
                @Valid
                BotCommandGroupMemberSaveDTO> members) {
    public record BotCommandGroupMemberSaveDTO(
            @NotBlank
            @Size(max = 80)
            String commandCode,
            @NotBlank
            @Size(max = 100)
            String title,
            int sortOrder,
            Map<String, String> parameterBindings) {
        public BotCommandGroupMemberSaveDTO {
            parameterBindings = parameterBindings == null ? Map.of() : Map.copyOf(parameterBindings);
        }
    }
}
