package com.nnm.bot.admin.dto.system;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MenuItem(
        Long id,
        Long parentId,
        String path,
        String name,
        String component,
        MenuMeta meta,
        List<MenuItem> children
) {
}
