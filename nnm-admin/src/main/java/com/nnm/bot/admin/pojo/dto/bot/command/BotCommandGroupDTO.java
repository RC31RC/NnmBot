package com.nnm.bot.admin.pojo.dto.bot.command;

import java.util.List;

public record BotCommandGroupDTO(Long id, String code, String name, boolean enabled, int version,
                                  List<BotCommandGroupMemberDTO> members) {
    public BotCommandGroupDTO {
        members = List.copyOf(members);
    }
}
