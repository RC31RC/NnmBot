package com.nnm.bot.warframe.persistent.official.pojo.vo.items;

import java.util.List;

public record WarframeMailVO(String senderKey, String sender, String titleKey, String title,
        String bodyKey, String body, String imageReference, String iconUrl, String transmission,
        Boolean highPriority, List<AttachmentVO> attachments, String customData, String cinematic) {
    /**
     * 构造邮件展示对象，并保存初始化参数。
     *
     * @param senderKey 发送者键。
     * @param sender 发送者。
     * @param titleKey 标题键。
     * @param title 标题。
     * @param bodyKey 正文键。
     * @param body 外部接口返回的响应正文。
     * @param imageReference 图片引用。
     * @param iconUrl 图标地址。
     * @param transmission 通信。
     * @param highPriority 高档优先级。
     * @param attachments 附件集合。
     * @param customData 自定义数据。
     * @param cinematic 过场动画。
     */
    public WarframeMailVO {
        attachments = List.copyOf(attachments);
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @return 按当前对象字段组成的文本内容。
     */
    public String text() {
        StringBuilder text = new StringBuilder(title == null ? "邮件" : title);
        if (sender != null) text.append(" · ").append(sender);
        if (body != null) text.append("\n").append(body);
        attachments.forEach(item -> text.append("\n附件：").append(item.name())
                .append(item.amount() == null ? " · 数量未提供" : " × " + item.amount()));
        return text.toString();
    }

    public record AttachmentVO(String itemKey, String name, Long amount, boolean counted) {
    }
}
