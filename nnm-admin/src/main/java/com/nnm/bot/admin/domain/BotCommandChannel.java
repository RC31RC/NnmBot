package com.nnm.bot.admin.domain;

import com.nnm.bot.core.constant.ChannelOutputMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bot_command_channel")
public class BotCommandChannel {

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属命令主键。 */
    @Column(name = "command_id", nullable = false)
    private Long commandId;

    /** 渠道平台编码。 */
    @Column(nullable = false, length = 32)
    private String platform;

    /** 该渠道使用的输出方式。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "output_mode", nullable = false, length = 20)
    private ChannelOutputMode outputMode;
}
