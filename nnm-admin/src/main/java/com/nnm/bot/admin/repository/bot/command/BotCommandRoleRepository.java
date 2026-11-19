package com.nnm.bot.admin.repository.bot.command;

import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** 查询命令与后台用户角色之间的授权关系。 */
public interface BotCommandRoleRepository extends JpaRepository<BotCommandRole, Long> {

    List<BotCommandRole> findAllByCommandId(Long commandId);

    void deleteAllByCommandId(Long commandId);
}
