package com.nnm.bot.admin.pojo.dto.bot.command;

import java.util.Map;
public record BotCommandGroupMemberDTO(String commandCode, String title, int sortOrder,
                                        Map<String, String> parameterBindings) { }
