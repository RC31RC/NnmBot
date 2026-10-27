package com.nnm.bot.warframe.controller.persistent.curated.forum;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumStoredFileDTO;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumFileVO;
import com.nnm.bot.warframe.persistent.curated.service.forum.ForumFileService;
import jakarta.annotation.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.concurrent.TimeUnit;

@RestController
public class ForumFileController {

    @Resource
    private ForumFileService fileService;

    /**
     * 接收上传内容并交给对应业务服务处理。
     *
     * @param file 本次上传或读取的文件。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping(
            value = "/api/admin/v1/warframe/forum/files/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission(
            value = {"warframe:forum:create", "warframe:forum:update"},
            mode = SaMode.OR)
    public ApiResponse<ForumFileVO> upload(
            @RequestPart("file")
            MultipartFile file) {
        return ApiResponse.ok(fileService.storeTemporary(file));
    }

    /**
     * 将永久文件读取结果转换为带媒体类型和缓存头的 HTTP 响应。
     * @param year 年份。
     * @param month 月份。
     * @param fileName 文件名称。
     * @return 响应实体对象。
     */
    @GetMapping("/api/public/v1/warframe/forum/files/{year}/{month}/{fileName:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> file(
            @PathVariable
            String year,
            @PathVariable
            String month,
            @PathVariable
            String fileName) {
        ForumStoredFileDTO storedFile =
                fileService.loadPermanent(year, month, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storedFile.contentType()))
                .contentLength(storedFile.size())
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
                .header("X-Content-Type-Options", "nosniff")
                .body(storedFile.resource());
    }

    /**
     * 读取允许访问的临时文件，并按实际文件类型返回 HTTP 响应。
     * @param year 年份。
     * @param month 月份。
     * @param fileName 文件名称。
     * @return 响应实体对象。
     */
    @GetMapping("/api/public/v1/warframe/forum/temp-files/{year}/{month}/{fileName:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> temporaryFile(
            @PathVariable
            String year,
            @PathVariable
            String month,
            @PathVariable
            String fileName) {
        ForumStoredFileDTO storedFile =
                fileService.loadTemporary(year, month, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storedFile.contentType()))
                .contentLength(storedFile.size())
                .cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .body(storedFile.resource());
    }
}
