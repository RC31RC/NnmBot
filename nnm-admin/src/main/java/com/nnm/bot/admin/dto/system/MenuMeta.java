package com.nnm.bot.admin.dto.system;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MenuMeta(
        String title,
        String icon,
        Boolean keepAlive,
        Boolean fixedTab,
        Boolean isHideTab,
        Boolean isFullPage,
        Boolean isIframe,
        String link,
        String activePath,
        String showTextBadge,
        Integer sort,
        Boolean isEnable,
        Boolean isHide,
        List<AuthItem> authList
) {
}
