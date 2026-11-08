package com.nnm.bot.warframe.persistent.official.client.source;

import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ImportStage;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest.FileEntry;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialSnapshotDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/** 逐文件校验同一 tag 的 manifest；每个文件独立下载、重试和回退，关闭时清理本轮下载。 */
@Slf4j
@Component
public class WarframeOfficialSnapshotDownloader {

    @Resource
    private WarframeOfficialSourceClient client;
    @Resource
    private WarframeOfficialImportProperties properties;

    /**
     * 下载指定外部数据并执行协议边界检查。
     *
     * @param ref 明确的官方数据 tag。
     * @param reporter 向管理端任务提交进度的回调。
     * @param paths 待处理文件的路径集合。
     * @return 游戏资料已下载快照对象。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    public WarframeOfficialSnapshotDTO download(String ref, TaskProgressReporter reporter, Set<String> paths)
        throws IOException {
        TaskProgressReporter progress = reporter == null ? TaskProgressReporter.noop() : reporter;
        progress.report(ImportStage.RESOLVING_VERSION.name(), "正在确认数据源固定 tag");
        long versionStarted = System.nanoTime();
        WarframeOfficialVersionDTO version = client.resolve(ref);
        log.info("官方数据版本确认完成: elapsedMs={}", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - versionStarted));
        Files.createDirectories(properties.getStagingDirectory());
        Path directory = Files.createTempDirectory(properties.getStagingDirectory(), "snapshot-");
        WarframeOfficialSnapshotDTO snapshot = new WarframeOfficialSnapshotDTO(version, null, directory);
        Set<URI> failedSources = new HashSet<>();
        long started = System.nanoTime();
        try {
            WarframeOfficialManifest manifest =
                client.manifest(version, directory.resolve("manifest.json.gz"), failedSources);
            snapshot = new WarframeOfficialSnapshotDTO(version, manifest, directory);
            int current = 0;
            int total = paths.size();
            for (FileEntry entry : manifest.files()) {
                if (!paths.contains(entry.path()))
                    continue;
                Path target = directory.resolve(entry.path());
                Files.createDirectories(target.getParent());
                progress.report(ImportStage.DOWNLOADING_DATASET.name(), "正在下载并校验 " + entry.path(), current, total);
                long fileStarted = System.nanoTime();
                client.download(version, entry, target, failedSources);
                log.info("官方压缩文件下载校验完成: path={}, elapsedMs={}", entry.path(),
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - fileStarted));
                progress.report(ImportStage.VERIFYING_DATASET.name(), "已完成 " + entry.path(), ++current, total);
            }
            BusinessAssert.equals(current, total, WarframeManagementErrorCode.DATASET_INVALID,
                "manifest 中缺少待下载的官方 JSON 文件");
            log.info("官方快照下载校验完成: files={}, elapsedMs={}", current,
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
            return snapshot;
        } catch (IOException | RuntimeException | Error failure) {
            log.warn("官方快照下载校验失败: elapsedMs={}", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started), failure);
            try {
                snapshot.close();
            } catch (IOException cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
            throw failure;
        }
    }
}
