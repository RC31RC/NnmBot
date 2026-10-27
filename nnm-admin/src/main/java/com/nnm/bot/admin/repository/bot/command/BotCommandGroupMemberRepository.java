package com.nnm.bot.admin.repository.bot.command;

import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandGroupMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotCommandGroupMemberRepository extends JpaRepository<BotCommandGroupMember, Long> {
    List<BotCommandGroupMember> findAllByOrderByGroupIdAscSortOrderAscIdAsc();
    List<BotCommandGroupMember> findAllByGroupIdOrderBySortOrderAscIdAsc(Long groupId);
    List<BotCommandGroupMember> findAllByCommandCodeIgnoreCaseOrderBySortOrderAscIdAsc(String commandCode);
    List<BotCommandGroupMember> findAllByCommandCodeIgnoreCase(String commandCode);
    void deleteAllByGroupId(Long groupId);
}
