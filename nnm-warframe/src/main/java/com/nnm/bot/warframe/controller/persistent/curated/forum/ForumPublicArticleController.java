package com.nnm.bot.warframe.controller.persistent.curated.forum;

import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumArticlePublicVO;
import com.nnm.bot.warframe.persistent.curated.service.forum.ForumArticleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/v1/warframe/forum/articles")
public class ForumPublicArticleController {

    @Resource
    private ForumArticleService service;

    /**
     * 读取指定对象的业务详情。
     *
     * @param id 目标记录的本地主键。
     * @return 以统一接口响应包装的本次操作结果。
     */
    @GetMapping("/{id}")
    public ApiResponse<ForumArticlePublicVO> detail(
            @PathVariable
            long id) {
        return ApiResponse.ok(service.publicDetail(id));
    }
}
