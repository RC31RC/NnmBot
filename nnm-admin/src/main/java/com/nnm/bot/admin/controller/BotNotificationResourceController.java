package com.nnm.bot.admin.controller;

import com.nnm.bot.admin.pojo.vo.bot.notification.BotNotificationResourceVO;
import com.nnm.bot.admin.controller.assembler.bot.notification.AdminBotNotificationViewAssembler;
import java.io.InputStream;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotNotificationResourceDTO;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.core.mq.message.notification.NotificationResource;
import com.nnm.bot.core.channel.notification.store.NotificationResourceStorage;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.UUID;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/v1/bot-notification-resources")
public class BotNotificationResourceController {
    @Resource
    private NotificationResourceStorage resources;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission("bot:notification:upload")
    public ApiResponse<BotNotificationResourceVO> upload(
            @RequestPart("file")
            MultipartFile file) throws IOException {
        try (InputStream input = file.getInputStream()) {
            NotificationResource resource = resources.upload(StpUtil.getLoginIdAsLong(), input, file.getSize());
            return ApiResponse.ok(AdminBotNotificationViewAssembler.toBotNotificationResourceVO(new BotNotificationResourceDTO(resource.id(), resource.contentType(),
                    resource.size(), resource.width(), resource.height(), resource.expiresAt())));
        }
    }

    @GetMapping("/{id}")
    @SaCheckPermission("bot:notification:preview")
    public ResponseEntity<InputStreamResource> image(
            @PathVariable
            UUID id) throws IOException {
        long owner = StpUtil.getLoginIdAsLong();
        NotificationResource resource = resources.owned(owner, id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(resource.contentType()))
                .contentLength(resource.size()).cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .body(new InputStreamResource(resources.openOwned(owner, id)));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("bot:notification:upload")
    public ApiResponse<Void> delete(
            @PathVariable
            UUID id) {
        resources.deleteOwned(StpUtil.getLoginIdAsLong(), id);
        return ApiResponse.ok();
    }
}
