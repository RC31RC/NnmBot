package com.nnm.bot.warframe.controller.persistent.curated.forum;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumFileVO;
import com.nnm.bot.warframe.persistent.curated.service.forum.ForumFileService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/warframe/forum/files")
public class ForumFileController {

    @Resource
    private ForumFileService fileService;

    /**
     * 接收上传内容并交给对应业务服务处理。
     *
     * @param file 本次上传或读取的文件。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission(value = {"warframe:forum:create", "warframe:forum:update"}, mode = SaMode.OR)
    public ApiResponse<ForumFileVO> upload(@RequestPart("file") MultipartFile file) {
        return ApiResponse.ok(fileService.storeTemporary(file));
    }

}
