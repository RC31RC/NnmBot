package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

import java.util.stream.Stream;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public record WarframeOfficialSnapshotDTO(
        WarframeOfficialVersionDTO version,
        WarframeOfficialManifest manifest,
        Path directory) implements AutoCloseable {

    /**
     * 释放当前组件持有的资源。
     *
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    @Override
    public void close() throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        } catch (UncheckedIOException exception) {
            throw exception.getCause();
        }
    }
}
