package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.channel.topology.BotTopologySnapshot.Binding;
import com.nnm.bot.core.channel.topology.BotTopologySnapshot.Group;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.service.BotGroupCommandContext;
import java.time.LocalDateTime;
import java.util.List;

/** 一次消息执行的只读视图；异步调用显式携带，写操作仍在自己的事务里读取权威数据。 */
public record CommandExecutionContext(Group groupView, Binding bindingView, List<Binding> bindings, Member member) {
    public CommandExecutionContext {
        bindings = List.copyOf(bindings);
    }

    public BotGroupCommandContext group() {
        return groupView == null ? null : new BotGroupCommandContext(groupView.entity(), bindingView.entity());
    }

    public record Member(Long id, long groupId, String qqId, String memberOpenid, String kookId,
                         boolean qqIdVerified, LocalDateTime qqBoundAt, String displayName, String role, boolean enabled) {
        public static Member of(BotGroupMember value) {
            return value == null ? null : new Member(value.getId(), value.getGroupId(), value.getQqId(),
                    value.getMemberOpenid(), value.getKookId(), value.isQqIdVerified(), value.getQqBoundAt(),
                    value.getDisplayName(), value.getMemberRole(), value.isEnabled());
        }

        public BotGroupMember entity() {
            BotGroupMember value = new BotGroupMember();
            value.setId(id);
            value.setGroupId(groupId);
            value.setQqId(qqId);
            value.setMemberOpenid(memberOpenid);
            value.setKookId(kookId);
            value.setQqIdVerified(qqIdVerified);
            value.setQqBoundAt(qqBoundAt);
            value.setDisplayName(displayName);
            value.setMemberRole(role);
            value.setEnabled(enabled);
            return value;
        }
    }
}
