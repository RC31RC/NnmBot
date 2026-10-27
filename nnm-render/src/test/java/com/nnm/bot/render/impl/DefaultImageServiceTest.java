package com.nnm.bot.render.impl;

import java.util.concurrent.ExecutorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.concurrent.BoundedTaskExecutor;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.render.RenderEngine;
import com.nnm.bot.render.TemplateRenderer;
import com.nnm.bot.render.config.RenderProperties;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefaultImageServiceTest {
    @Test
    void templateAndEngineRunOnPlatformWorkerAndBusinessFailureIsPreserved() {
        try (ExecutorService workers = Executors.newFixedThreadPool(1);
             BoundedTaskExecutor tasks = new BoundedTaskExecutor(new TaskExecutorAdapter(workers), 2,
                     Duration.ofSeconds(1), Duration.ofSeconds(2))) {
            TemplateRenderer renderer = mock(TemplateRenderer.class);
            RenderEngine engine = mock(RenderEngine.class);
            AtomicBoolean platform = new AtomicBoolean();
            when(renderer.renderHtml(any(), any())).thenAnswer(call -> {
                platform.set(!Thread.currentThread().isVirtual());
                return "<html/>";
            });
            when(engine.renderHtmlToImage(any(), any())).thenReturn(new byte[] {1, 2});
            DefaultImageService service = service(tasks, renderer, engine, new RenderProperties());
            assertThat(service.generateHtml("<html/>", Map.of("name", "value"))).containsExactly(1, 2);
            assertThat(platform).isTrue();
            BusinessException failure = new BusinessException(503, "busy");
            when(engine.renderHtmlToImage(any(), any())).thenThrow(failure);
            assertThatThrownBy(() -> service.renderHtml("<html/>", Map.of())).isSameAs(failure);
        }
    }

    @Test
    void rejectsOversizedVariablesBeforeSchedulingAndBoundsEngineResult() {
        RenderProperties properties = new RenderProperties();
        properties.getLimits().setMaxInputBytes(64);
        properties.getLimits().setMaxResultBytes(1);
        TemplateRenderer renderer = mock(TemplateRenderer.class);
        RenderEngine engine = mock(RenderEngine.class);
        try (BoundedTaskExecutor tasks = new BoundedTaskExecutor(new TaskExecutorAdapter(Runnable::run), 1,
                Duration.ofSeconds(1), Duration.ofSeconds(2))) {
            DefaultImageService service = service(tasks, renderer, engine, properties);
            assertThatThrownBy(() -> service.generateHtml("<html/>", Map.of("text", "x".repeat(100))))
                    .isInstanceOf(BusinessException.class).hasMessageContaining("输入");
            verifyNoInteractions(renderer, engine);
            when(renderer.renderHtml(any(), any())).thenReturn("<html/>");
            when(engine.renderHtmlToImage(any(), any())).thenReturn(new byte[] {1, 2});
            assertThatThrownBy(() -> service.generateHtml("<html/>", Map.of()))
                    .isInstanceOf(BusinessException.class).hasMessageContaining("结果");
        }
    }

    private DefaultImageService service(BoundedTaskExecutor tasks, TemplateRenderer renderer,
                                        RenderEngine engine, RenderProperties properties) {
        DefaultImageService service = new DefaultImageService();
        ReflectionTestUtils.setField(service, "taskExecutor", tasks);
        ReflectionTestUtils.setField(service, "properties", properties);
        ReflectionTestUtils.setField(service, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(service, "templateRenderer", renderer);
        ReflectionTestUtils.setField(service, "renderEngine", engine);
        return service;
    }
}
