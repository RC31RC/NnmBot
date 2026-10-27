package com.nnm.bot.admin.pojo.vo.bot.command;

import java.util.List;

public record BotCommandGroupVO(Long id, String code, String name, boolean enabled, int version,
                                  List<BotCommandGroupMemberVO> members) {
    public BotCommandGroupVO {
        members = List.copyOf(members);
    }
}
