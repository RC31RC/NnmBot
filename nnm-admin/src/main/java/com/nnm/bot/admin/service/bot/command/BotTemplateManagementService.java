package com.nnm.bot.admin.service.bot.command;

import com.nnm.bot.admin.pojo.dto.bot.command.BotTemplateDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotTemplateSaveDTO;

import java.util.List;

/** 管理命令展示模板及其命令关联。 */
public interface BotTemplateManagementService {

    /**
     * 查询模板及其当前引用数量。
     *
     * @return 模板列表
     */
    List<BotTemplateDTO> list();

    /**
     * 创建并校验展示模板。
     *
     * @param request 模板内容
     * @return 新模板主键
     */
    long create(BotTemplateSaveDTO request);

    /**
     * 更新指定展示模板。
     *
     * @param id 模板主键
     * @param request 模板内容
     */
    void update(long id, BotTemplateSaveDTO request);

    /**
     * 删除未被命令引用的模板。
     *
     * @param id 模板主键
     */
    void delete(long id);
}
