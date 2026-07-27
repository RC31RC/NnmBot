package com.nnm.bot.admin.dto.system;

import java.time.LocalDateTime;
import java.util.List;

public record UserListItem(
        Long id,
        String avatar,
        String status,
        String userName,
        String userGender,
        String nickName,
        String userPhone,
        String userEmail,
        List<String> userRoles,
        String createBy,
        LocalDateTime createTime,
        String updateBy,
        LocalDateTime updateTime
) {
}
