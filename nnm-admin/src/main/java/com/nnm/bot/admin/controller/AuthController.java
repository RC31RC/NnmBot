package com.nnm.bot.admin.controller;

import com.nnm.bot.admin.pojo.dto.auth.LoginDTO;
import com.nnm.bot.admin.pojo.vo.auth.LoginTokenVO;
import com.nnm.bot.admin.controller.assembler.auth.AdminAuthViewAssembler;
import com.nnm.bot.admin.pojo.dto.auth.LoginTokenDTO;
import com.nnm.bot.admin.pojo.dto.auth.RefreshTokenDTO;
import com.nnm.bot.admin.service.auth.AuthService;
import com.nnm.bot.common.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/v1/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginTokenVO> login(
            @Valid
            @RequestBody
    LoginDTO request) {
        return ApiResponse.ok(AdminAuthViewAssembler.toLoginTokenVO(authService.login(request)));
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginTokenVO> refresh(
            @Valid
            @RequestBody
    RefreshTokenDTO request) {
        return ApiResponse.ok(AdminAuthViewAssembler.toLoginTokenVO(authService.refresh(request)));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.ok();
    }
}
