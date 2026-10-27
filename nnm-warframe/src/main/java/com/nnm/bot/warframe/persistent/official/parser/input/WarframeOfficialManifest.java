package com.nnm.bot.warframe.persistent.official.parser.input;

import java.time.Instant;
import java.util.List;

/** 数据源发布清单；只携带成品契约，不传播采集来源。 */
public record WarframeOfficialManifest(
        int schemaVersion,
        String buildId,
        Instant createdAt,
        String producer,
        List<FileEntry> files) {

    /**
     * 构造数据文件发布清单，并保存初始化参数。
     *
     * @param schemaVersion 字段合同版本。
     * @param buildId 构造标识。
     * @param createdAt 创建时间时刻。
     * @param producer 生产者。
     * @param files 文件集合。
     */
    public WarframeOfficialManifest {
        files = files == null ? List.of() : List.copyOf(files);
    }

    public record FileEntry(
            String path,
            String sha256,
            long bytes,
            String mediaType,
            Long recordCount) {
    }
}
