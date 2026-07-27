package com.nnm.bot.admin.dto.system;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UserSaveRequest(
        @NotBlank @Size(max = 50) String userName,
        @Size(min = 6, max = 100) String password,
        @Size(max = 100) String nickName,
        @Size(max = 30) String userPhone,
        @Email @Size(max = 120) String userEmail,
        @Size(max = 20) String userGender,
        @Size(max = 500) String avatar,
        Boolean enabled,
        Set<String> roleCodes
) {
}
