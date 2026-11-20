package com.nnm.bot.admin.service.bot.channel;

import com.nnm.bot.admin.pojo.dto.bot.channel.BotChannelInstanceDTO;
import com.nnm.bot.admin.pojo.query.bot.channel.OneBotAccountProbeQuery;
import com.nnm.bot.admin.pojo.dto.bot.channel.OneBotAccountProbeDTO;
import com.nnm.bot.admin.pojo.dto.bot.channel.OneBotInstanceSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.channel.QqOfficialInstanceSaveDTO;

import java.util.List;

/** 管理机器人渠道实例配置及其运行状态。 */
public interface BotChannelManagementService {

    /**
     * 查询渠道实例及当前运行状态。
     *
     * @return 按平台和实例标识排序的实例列表
     */
    List<BotChannelInstanceDTO> instances();

    /**
     * 创建 OneBot 渠道实例。
     *
     * @param request 实例配置
     * @return 新实例主键
     */
    long createOneBot(OneBotInstanceSaveDTO request);

    /**
     * 更新 OneBot 渠道实例。
     *
     * @param id 实例主键
     * @param request 更新后的实例配置
     */
    void updateOneBot(long id, OneBotInstanceSaveDTO request);

    /**
     * 创建 QQ 官方机器人渠道实例。
     *
     * @param request 实例配置
     * @return 新实例主键
     */
    long createQqOfficial(QqOfficialInstanceSaveDTO request);

    /**
     * 更新 QQ 官方机器人渠道实例。
     *
     * @param id 实例主键
     * @param request 更新后的实例配置
     */
    void updateQqOfficial(long id, QqOfficialInstanceSaveDTO request);

    /**
     * 删除渠道实例。
     *
     * @param id 实例主键
     */
    void delete(long id);

    /**
     * 启动指定渠道实例。
     *
     * @param id 实例主键
     */
    void start(long id);

    /**
     * 停止指定渠道实例。
     *
     * @param id 实例主键
     */
    void stop(long id);

    /**
     * 探测 OneBot 连接并读取账号信息。
     *
     * @param request 探测目标和连接参数
     * @return 探测到的账号信息
     */
    OneBotAccountProbeDTO probeOneBot(OneBotAccountProbeQuery request);
}
