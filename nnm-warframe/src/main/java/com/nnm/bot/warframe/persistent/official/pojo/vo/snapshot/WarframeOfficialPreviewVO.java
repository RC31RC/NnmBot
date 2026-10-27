package com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot;

import java.time.Instant;
import java.util.List;

/** 文件级完整快照审核；不把尚未迁移的业务域标成结构化入库完成。 */
public record WarframeOfficialPreviewVO(
        String id,
        WarframeOfficialVersionVO version,
        String buildId,
        String baselineId,
        String status,
        Instant expiresAt,
        long totalBytes,
        int addedRecords,
        int changedRecords,
        int removedRecords,
        List<String> unmappedPaths,
        List<FileChangeVO> files) {
    /**
     * 构造数据文件预览展示对象，并保存初始化参数。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @param version 本次操作已经确定的数据版本。
     * @param buildId 构造标识。
     * @param baselineId 采纳基线标识。
     * @param status 状态。
     * @param expiresAt 过期时间时刻。
     * @param totalBytes 总数字节。
     * @param addedRecords 新增记录。
     * @param changedRecords 已变化记录。
     * @param removedRecords 移除的记录。
     * @param unmappedPaths 未映射路径集合。
     * @param files 文件集合。
     */
    public WarframeOfficialPreviewVO {
        files = List.copyOf(files);
        unmappedPaths = List.copyOf(unmappedPaths);
    }

    public record FileChangeVO(String path, String sha256, long bytes, Long recordCount, String changeType) {
    }
}
