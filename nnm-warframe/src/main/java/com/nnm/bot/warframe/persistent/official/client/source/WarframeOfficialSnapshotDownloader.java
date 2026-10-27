package com.nnm.bot.warframe.persistent.official.client.source;

import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotValidator;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialSnapshotDTO;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest.FileEntry;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.persistent.official.client.source.WarframeOfficialSourceClient;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/** 校验同一固定 manifest 的所选文件；关闭时清理本轮下载。 */
@Component
public class WarframeOfficialSnapshotDownloader {

    @Resource
    private WarframeOfficialSourceClient client;
    @Resource
    private WarframeOfficialSnapshotValidator validator;
    @Resource
    private WarframeOfficialImportProperties properties;

    /**
     * 下载指定外部数据并执行协议边界检查。
     *
     * @param ref 明确的标签名称或完整提交哈希。
     * @param reporter 向管理端任务提交进度的回调。
     * @param paths 待处理文件的路径集合。
     * @return 游戏资料已下载快照对象。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    public WarframeOfficialSnapshotDTO download(String ref, TaskProgressReporter reporter, Set<String> paths) throws IOException {
        TaskProgressReporter progress = reporter == null ? TaskProgressReporter.noop() : reporter;
        progress.report("RESOLVING_VERSION", "正在确认数据源固定版本");
        WarframeOfficialVersionDTO version = client.resolve(ref);
        byte[] manifestBytes = client.manifest(version);
        WarframeOfficialManifest manifest = validator.manifest(manifestBytes);
        Files.createDirectories(properties.getStagingDirectory());
        Path directory = Files.createTempDirectory(properties.getStagingDirectory(), "snapshot-");
        WarframeOfficialSnapshotDTO snapshot = new WarframeOfficialSnapshotDTO(version, manifest, directory);
        try {
            int current = 0;
            int total = paths.size();
            for (FileEntry entry : manifest.files()) {
                if (!paths.contains(entry.path())) continue;
                Path target = directory.resolve(entry.path());
                Files.createDirectories(target.getParent());
                progress.report("DOWNLOADING_DATASET", "正在校验 " + entry.path(), current, total);
                client.download(version, entry.path(), target, entry.bytes());
                validator.file(entry, target);
                progress.report("VERIFYING_DATASET", "已校验 " + entry.path(), ++current, total);
            }
            return snapshot;
        } catch (IOException | RuntimeException | Error failure) {
            try {
                snapshot.close();
            } catch (IOException cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
            throw failure;
        }
    }

}
