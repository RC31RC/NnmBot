package com.nnm.bot.admin.pojo.vo.bot.command;

import java.util.Map;
public record BotCommandGroupMemberVO(String commandCode, String title, int sortOrder,
                                        Map<String, String> parameterBindings) { }
