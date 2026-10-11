package com.nnm.bot.warframe.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.warframe.application.WarframeForumFileService;
import com.nnm.bot.warframe.pojo.WarframeForumFileItem;
import jakarta.annotation.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RestController
public class WarframeForumFileController {

    @Resource
    private WarframeForumFileService fileService;

    @PostMapping(
            value = "/api/admin/v1/warframe/forum/files/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission(
            value = {"warframe:forum:create", "warframe:forum:update"},
            mode = SaMode.OR)
    public ApiResponse<WarframeForumFileItem> upload(
            @RequestPart("file")
            MultipartFile file) {
        return ApiResponse.ok(fileService.storeTemporary(file));
    }

    @GetMapping("/api/public/v1/warframe/forum/files/{year}/{month}/{fileName:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> file(
            @PathVariable
            String year,
            @PathVariable
            String month,
            @PathVariable
            String fileName) {
        WarframeForumFileService.StoredFile storedFile =
                fileService.loadPermanent(year, month, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storedFile.contentType()))
                .contentLength(storedFile.size())
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
                .header("X-Content-Type-Options", "nosniff")
                .body(storedFile.resource());
    }

    @GetMapping("/api/public/v1/warframe/forum/temp-files/{year}/{month}/{fileName:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> temporaryFile(
            @PathVariable
            String year,
            @PathVariable
            String month,
            @PathVariable
            String fileName) {
        WarframeForumFileService.StoredFile storedFile =
                fileService.loadTemporary(year, month, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storedFile.contentType()))
                .contentLength(storedFile.size())
                .cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .body(storedFile.resource());
    }
}
