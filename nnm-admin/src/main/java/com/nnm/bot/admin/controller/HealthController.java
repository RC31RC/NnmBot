package com.nnm.bot.admin.controller;

import com.nnm.bot.common.response.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 暴露管理端应用健康状态。 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @PostMapping
    public ApiResponse<String> health() {
        return ApiResponse.ok("NnmBot is running");
    }
}
