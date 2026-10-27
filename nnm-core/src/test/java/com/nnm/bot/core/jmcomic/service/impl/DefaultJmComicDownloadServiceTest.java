package com.nnm.bot.core.jmcomic.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;
import org.mockito.InOrder;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.jmcomic.config.JmComicProperties;
import com.nnm.bot.core.jmcomic.constant.JmComicErrorCode;
import com.nnm.bot.core.jmcomic.exception.JmComicWholeFileException;
import com.nnm.bot.core.jmcomic.model.JmComicDocument;
import com.nnm.bot.core.jmcomic.service.JmComicPdfService;
import com.nnm.bot.core.jmcomic.service.JmComicWholeFileService;
import io.github.jukomu.jmcomic.api.download.DownloadRequest;
import io.github.jukomu.jmcomic.api.download.DownloadResult;
import io.github.jukomu.jmcomic.api.model.JmAlbum;
import io.github.jukomu.jmcomic.api.model.JmUserInfo;
import io.github.jukomu.jmcomic.core.client.AbstractJmClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class DefaultJmComicDownloadServiceTest {

    @TempDir
    private Path temporaryDirectory;

    private JmComicProperties properties;
    private AbstractJmClient client;
    private ObjectProvider<AbstractJmClient> clientProvider;
    private JmComicWholeFileService wholeFileService;
    private JmComicPdfService pdfService;
    private DefaultJmComicDownloadService service;

    @BeforeEach
    void setUp() {
        this.properties = new JmComicProperties();
        this.properties.setEnabled(true);
        this.properties.setTemporaryDirectory(this.temporaryDirectory);
        this.client = mock(AbstractJmClient.class);
        this.clientProvider = mock(ObjectProvider.class);
        when(this.clientProvider.getObject()).thenReturn(this.client);
        JmAlbum album = mock(JmAlbum.class);
        when(album.getTitle()).thenReturn("测试漫画");
        when(album.getPageCount()).thenReturn(1);
        when(this.client.getAlbum(anyString())).thenReturn(album);
        this.wholeFileService = mock(JmComicWholeFileService.class);
        this.pdfService = mock(JmComicPdfService.class);
        this.service = new DefaultJmComicDownloadService();
        ReflectionTestUtils.setField(this.service, "properties", this.properties);
        ReflectionTestUtils.setField(this.service, "jmClientProvider", this.clientProvider);
        ReflectionTestUtils.setField(this.service, "wholeFileService", this.wholeFileService);
        ReflectionTestUtils.setField(this.service, "pdfService", this.pdfService);
        this.service.initialize();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldPreferWholeFileWithoutDownloadingPages(boolean pagesEnabled) {
        this.properties.setPageDownloadEnabled(pagesEnabled);
        when(this.wholeFileService.download(any(), anyString(), anyString(), any()))
                .thenAnswer(invocation -> {
                    Path workspace = invocation.getArgument(3);
                    return Optional.of(new JmComicDocument(
                            "123", "整本结果", 1, workspace.resolve("whole.pdf"), workspace));
                });

        JmComicDocument document = this.service.downloadAsPdf("123");

        assertThat(document.title()).isEqualTo("整本结果");
        assertThat(document.pdfPath().getFileName().toString()).isEqualTo("whole.pdf");
        verify(this.client, never()).download(any(JmAlbum.class));
        verifyNoInteractions(this.pdfService);
    }

    @Test
    void shouldDownloadPagesOnlyAndSortChaptersAndPagesNaturally() throws IOException {
        this.properties.setWholeFileEnabled(false);
        Path chapterTen = this.createDownloadedImage("10_title/12/1.png");
        Path pageTen = this.createDownloadedImage("02_title/900/10.png");
        Path pageTwo = this.createDownloadedImage("02_title/900/2.png");
        Path chapterOne = this.createDownloadedImage("01_title/999/1.png");
        this.preparePageDownload(List.of(chapterTen, pageTen, pageTwo, chapterOne));

        JmComicDocument document = this.service.downloadAsPdf("123");

        assertThat(document.pageCount()).isEqualTo(4);
        verifyNoInteractions(this.wholeFileService);
        verify(this.pdfService).createCompressedPdf(
                eq(List.of(chapterOne, pageTwo, pageTen, chapterTen)), eq(document.pdfPath()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldFallBackToPagesWhenWholeFileIsUnavailable(boolean wholeFileThrows)
            throws IOException {
        this.makeWholeFileUnavailable(wholeFileThrows);
        this.preparePageDownload(List.of(this.createDownloadedImage("1.png")));

        JmComicDocument document = this.service.downloadAsPdf("123");

        assertThat(document.pageCount()).isEqualTo(1);
        InOrder order = inOrder(this.wholeFileService, this.client);
        order.verify(this.wholeFileService).download(
                eq(this.client), eq("123"), anyString(), any(Path.class));
        order.verify(this.client).download(any(JmAlbum.class));
        verify(this.pdfService).createCompressedPdf(any(), eq(document.pdfPath()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldNotFallBackWhenPageDownloadIsDisabled(boolean wholeFileThrows)
            throws IOException {
        this.properties.setPageDownloadEnabled(false);
        JmComicWholeFileException wholeFileFailure =
                this.makeWholeFileUnavailable(wholeFileThrows);

        assertThatThrownBy(() -> this.service.downloadAsPdf("123"))
                .isInstanceOfSatisfying(BusinessException.class, exception -> {
                    assertThat(exception.getMessage()).contains("分页下载已关闭");
                    assertThat(exception.getCause()).isSameAs(wholeFileFailure);
                });

        verify(this.client, never()).download(any(JmAlbum.class));
        verifyNoInteractions(this.pdfService);
        this.assertWorkspacesCleanedUp();
    }

    @Test
    void shouldRejectBeforeInitializingClientWhenBothMethodsAreDisabled() throws IOException {
        this.properties.setWholeFileEnabled(false);
        this.properties.setPageDownloadEnabled(false);

        assertThatThrownBy(() -> this.service.downloadAsPdf("123"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("整本下载和分页下载均已关闭");

        verifyNoInteractions(
                this.clientProvider, this.client, this.wholeFileService, this.pdfService);
        this.assertWorkspacesCleanedUp();
    }

    @Test
    void shouldNotFallBackToBypassResourceLimits() throws IOException {
        BusinessException failure = new BusinessException(JmComicErrorCode.DOWNLOAD_TOO_LARGE);
        when(this.wholeFileService.download(any(), anyString(), anyString(), any()))
                .thenThrow(failure);

        assertThatThrownBy(() -> this.service.downloadAsPdf("123")).isSameAs(failure);

        verify(this.client, never()).download(any(JmAlbum.class));
        verifyNoInteractions(this.pdfService);
        this.assertWorkspacesCleanedUp();
    }

    @Test
    void shouldKeepBothFailureCausesWhenBothMethodsFail() throws IOException {
        JmComicWholeFileException wholeFileFailure = this.makeWholeFileUnavailable(true);
        DownloadRequest request = this.preparePageDownload(List.of());
        IllegalStateException pageFailure = new IllegalStateException("分页网络失败");
        when(request.execute()).thenThrow(pageFailure);

        assertThatThrownBy(() -> this.service.downloadAsPdf("123"))
                .isInstanceOf(BusinessException.class)
                .hasCause(pageFailure);

        assertThat(pageFailure.getSuppressed()).containsExactly(wholeFileFailure);
        this.assertWorkspacesCleanedUp();
    }

    @Test
    void shouldQueueTasksWithSharedClientAndUseIndependentWorkspaces() throws Exception {
        CountDownLatch enteredDownloads = new CountDownLatch(2);
        CountDownLatch releaseDownloads = new CountDownLatch(1);
        AbstractJmClient client = mock(AbstractJmClient.class);
        JmAlbum album = mock(JmAlbum.class);
        JmComicWholeFileService wholeFileService = mock(JmComicWholeFileService.class);
        when(client.login("user", "password")).thenReturn(JmUserInfo.partial("user"));
        when(client.getAlbum(anyString())).thenAnswer(invocation -> {
            enteredDownloads.countDown();
            if (!releaseDownloads.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("test download release timed out");
            }
            return album;
        });
        when(album.getTitle()).thenReturn("并发测试");
        when(album.getPageCount()).thenReturn(1);
        when(wholeFileService.download(any(), anyString(), anyString(), any()))
                .thenAnswer(invocation -> {
                    String albumId = invocation.getArgument(1);
                    Path workspace = invocation.getArgument(3);
                    return Optional.of(new JmComicDocument(
                            albumId,
                            "并发测试",
                            1,
                            workspace.resolve(albumId + ".pdf"),
                            workspace));
                });
        DefaultJmComicDownloadService service =
                this.createService(client, wholeFileService);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<JmComicDocument> first =
                    executor.submit(() -> service.downloadAsPdf("10001"));
            Future<JmComicDocument> second =
                    executor.submit(() -> service.downloadAsPdf("10002"));

            assertThat(enteredDownloads.await(5, TimeUnit.SECONDS)).isTrue();
            Future<JmComicDocument> third =
                    executor.submit(() -> service.downloadAsPdf("10003"));
            assertThat(third).isNotDone();

            releaseDownloads.countDown();
            JmComicDocument firstDocument = first.get(5, TimeUnit.SECONDS);
            JmComicDocument secondDocument = second.get(5, TimeUnit.SECONDS);
            JmComicDocument thirdDocument = third.get(5, TimeUnit.SECONDS);
            assertThat(firstDocument.workspace()).isNotEqualTo(secondDocument.workspace());
            assertThat(thirdDocument.workspace())
                    .isNotEqualTo(firstDocument.workspace())
                    .isNotEqualTo(secondDocument.workspace());
        } finally {
            releaseDownloads.countDown();
        }

        verify(client).login("user", "password");
        verify(client, times(3)).getAlbum(anyString());
    }

    @Test
    void disabledDownloadDoesNotInitializeClient() {
        JmComicProperties properties = new JmComicProperties();
        properties.setEnabled(false);
        ObjectProvider<AbstractJmClient> clientProvider = mock(ObjectProvider.class);

        DefaultJmComicDownloadService service = new DefaultJmComicDownloadService();
        ReflectionTestUtils.setField(service, "properties", properties);
        ReflectionTestUtils.setField(service, "jmClientProvider", clientProvider);

        assertThatThrownBy(() -> service.downloadAsPdf("10001"))
                .isInstanceOf(BusinessException.class);
        verifyNoInteractions(clientProvider);
    }

    private DefaultJmComicDownloadService createService(
            AbstractJmClient client, JmComicWholeFileService wholeFileService) {
        JmComicProperties properties = new JmComicProperties();
        properties.setEnabled(true);
        properties.setTemporaryDirectory(this.temporaryDirectory);
        properties.setMaxConcurrentTasks(2);
        properties.setUsername("user");
        properties.setPassword("password");

        DefaultJmComicDownloadService service = new DefaultJmComicDownloadService();
        ReflectionTestUtils.setField(service, "properties", properties);
        ReflectionTestUtils.setField(service, "pdfService", mock(JmComicPdfService.class));
        ReflectionTestUtils.setField(service, "wholeFileService", wholeFileService);
        ObjectProvider<AbstractJmClient> clientProvider = mock(ObjectProvider.class);
        when(clientProvider.getObject()).thenReturn(client);
        ReflectionTestUtils.setField(service, "jmClientProvider", clientProvider);
        service.initialize();
        return service;
    }

    private JmComicWholeFileException makeWholeFileUnavailable(boolean throwsException) {
        if (throwsException) {
            JmComicWholeFileException failure = new JmComicWholeFileException("整本网络失败");
            when(this.wholeFileService.download(any(), anyString(), anyString(), any()))
                    .thenThrow(failure);
            return failure;
        }
        when(this.wholeFileService.download(any(), anyString(), anyString(), any()))
                .thenReturn(Optional.empty());
        return null;
    }

    private DownloadRequest preparePageDownload(List<Path> images) {
        DownloadRequest request = mock(DownloadRequest.class);
        when(this.client.download(any(JmAlbum.class))).thenReturn(request);
        when(request.withPath(any(Path.class))).thenReturn(request);
        when(request.execute()).thenReturn(new DownloadResult(images, Map.of()));
        return request;
    }

    private Path createDownloadedImage(String relativePath) throws IOException {
        Path image = this.temporaryDirectory.resolve("fixture").resolve(relativePath);
        Files.createDirectories(image.getParent());
        return Files.createFile(image);
    }

    private void assertWorkspacesCleanedUp() throws IOException {
        try (Stream<Path> children = Files.list(this.temporaryDirectory)) {
            assertThat(children).noneMatch(path -> path.getFileName().toString().startsWith("album-"));
        }
    }
}
