package com.nnm.bot.warframe.service;

import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.client.WarframeCommunityApiClient;
import com.nnm.bot.warframe.client.WarframeCommunityApiClient.FetchedText;
import com.nnm.bot.warframe.config.properties.WarframeCommunityDataProperties.DatasetSource;
import com.nnm.bot.warframe.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.constant.WarframeCommunitySource;
import com.nnm.bot.warframe.constant.WarframeCommunitySourceStatus;
import com.nnm.bot.warframe.pojo.WarframeCommunitySourceSummary;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;

/** 只协调来源下载和纯解析；数据库比对、快照发布和合并仍由数据集服务负责。 */
@Component
@Slf4j
public class WarframeCommunitySourceLoader {

    @Resource
    private WarframeCommunityApiClient apiClient;
    @Resource(name = "communitySourceExecutor")
    private Executor sourceExecutor;

    public <T> List<LoadedSource<T>> load(
            WarframeCommunityDataset dataset,
            List<DatasetSource> sources,
            BiFunction<DatasetSource, String, T> parser,
            ToIntFunction<T> recordCounter,
            TaskProgressReporter progress) {
        sources.forEach(source -> WarframeCommunitySource.parse(
                dataset.code(), source.getSourceCode()));
        ExecutorCompletionService<LoadedSource<T>> completion =
                new ExecutorCompletionService<>(sourceExecutor);
        List<Future<LoadedSource<T>>> futures = new ArrayList<>();
        List<LoadedSource<T>> results = new ArrayList<>();
        try {
            progress.report("DOWNLOADING", "正在拉取" + dataset.label(), 0, sources.size());
            for (int index = 0; index < sources.size(); index++) {
                int sourceIndex = index;
                DatasetSource source = sources.get(index);
                futures.add(completion.submit(
                        () -> loadOne(sourceIndex, dataset, source, parser, recordCounter)));
            }
            // 由协调线程统一报告完成进度和心跳，避免并发 reporter 把进度写回旧值。
            while (results.size() < sources.size()) {
                Future<LoadedSource<T>> finished = completion.poll(5, TimeUnit.SECONDS);
                if (finished != null) {
                    results.add(finished.get());
                }
                progress.report("DOWNLOADING",
                        dataset.label() + "来源处理 " + results.size() + "/" + sources.size(),
                        results.size(), sources.size());
            }
            results.sort(Comparator.comparingInt(LoadedSource::index));
            return List.copyOf(results);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("社区来源加载被中断", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("社区来源加载异常", exception.getCause());
        } finally {
            futures.stream().filter(future -> !future.isDone())
                    .forEach(future -> future.cancel(true));
        }
    }

    private <T> LoadedSource<T> loadOne(
            int index,
            WarframeCommunityDataset dataset,
            DatasetSource source,
            BiFunction<DatasetSource, String, T> parser,
            ToIntFunction<T> recordCounter) {
        long startedAt = System.nanoTime();
        FetchedText fetched = null;
        try {
            fetched = apiClient.fetch(source.getSourceCode(), source.getCandidateUrls());
            T value = parser.apply(source, fetched.body());
            int count = recordCounter.applyAsInt(value);
            log.info("社区来源解析完成, dataset={}, source={}, records={}, elapsedMs={}",
                    dataset.code(), source.getSourceCode(), count, elapsedMillis(startedAt));
            return new LoadedSource<>(index, value, new WarframeCommunitySourceSummary(
                    source.getSourceCode(), WarframeCommunitySourceStatus.SUCCESS,
                    WarframeCommunityApiClient.safeEndpoint(fetched.endpoint()),
                    elapsedMillis(startedAt), count, null, 0, null));
        } catch (RuntimeException exception) {
            log.warn("社区来源处理失败，保留该来源现有数据, dataset={}, source={}, message={}",
                    dataset.code(), source.getSourceCode(),
                    WarframeCommunityApiClient.failureMessage(exception));
            log.debug("社区来源处理失败详情, dataset={}, source={}",
                    dataset.code(), source.getSourceCode(), exception);
            return new LoadedSource<>(index, null, new WarframeCommunitySourceSummary(
                    source.getSourceCode(), WarframeCommunitySourceStatus.FAILED,
                    fetched == null ? null
                            : WarframeCommunityApiClient.safeEndpoint(fetched.endpoint()),
                    elapsedMillis(startedAt), 0,
                    WarframeCommunityApiClient.failureMessage(exception), 0, null));
        }
    }

    private static long elapsedMillis(long startedAt) {
        return Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
    }

    public record LoadedSource<T>(int index, T value, WarframeCommunitySourceSummary summary) {
        public boolean successful() {
            return summary.status() == WarframeCommunitySourceStatus.SUCCESS;
        }
    }
}
