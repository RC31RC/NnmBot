package com.nnm.bot.core.task;

import com.nnm.bot.common.task.*;

import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryAdminTaskServiceTest {

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final InMemoryAdminTaskService service = newService();

    @AfterEach
    void closeExecutor() {
        executor.close();
    }

    @Test
    void publishesBusinessErrorCodeAndMessage() throws Exception {
        AdminTaskSubmission submission = service.submit(
                "TEST_BUSINESS",
                "test:task:read",
                ignored -> {
                    throw new BusinessException(422, "业务输入不完整");
                });

        AdminTaskEvent event = awaitTerminal(submission);

        assertThat(event.status()).isEqualTo(AdminTaskStatus.FAILED);
        assertThat(event.errorCode()).isEqualTo(422);
        assertThat(event.errorType()).isEqualTo("BUSINESS");
        assertThat(event.error()).isEqualTo("业务输入不完整");
    }

    @Test
    void hidesSystemExceptionInternalMessageFromTaskEvent() throws Exception {
        AdminTaskSubmission submission = service.submit(
                "TEST_SYSTEM",
                "test:task:read",
                ignored -> {
                    throw new SystemException(
                            CommonErrorCode.SYSTEM_ERROR,
                            "internal url and credentials",
                            new IllegalStateException("cause"));
                });

        AdminTaskEvent event = awaitTerminal(submission);

        assertThat(event.status()).isEqualTo(AdminTaskStatus.FAILED);
        assertThat(event.errorCode()).isEqualTo(CommonErrorCode.SYSTEM_ERROR.getCode());
        assertThat(event.errorType()).isEqualTo("SYSTEM");
        assertThat(event.error()).isEqualTo(CommonErrorCode.SYSTEM_ERROR.getMessage());
        assertThat(event.error()).doesNotContain("credentials");
    }

    @Test
    void mapsUnexpectedFailureToSystemError() throws Exception {
        AdminTaskSubmission submission = service.submit(
                "TEST_UNEXPECTED",
                "test:task:read",
                ignored -> {
                    throw new IllegalStateException("internal details");
                });

        AdminTaskEvent event = awaitTerminal(submission);

        assertThat(event.errorCode()).isEqualTo(CommonErrorCode.SYSTEM_ERROR.getCode());
        assertThat(event.errorType()).isEqualTo("SYSTEM");
        assertThat(event.error()).isEqualTo(CommonErrorCode.SYSTEM_ERROR.getMessage());
    }

    @Test
    void unwrapsCompletionExceptionAndKeepsBusinessError() throws Exception {
        AdminTaskSubmission submission = service.submit(
                "TEST_COMPLETION_BUSINESS",
                "test:task:read",
                ignored -> {
                    throw new CompletionException(new BusinessException(422, "确认参数"));
                });

        AdminTaskEvent event = awaitTerminal(submission);

        assertThat(event.errorCode()).isEqualTo(422);
        assertThat(event.errorType()).isEqualTo("BUSINESS");
        assertThat(event.error()).isEqualTo("确认参数");
    }

    @Test
    void unwrapsExecutionExceptionAndKeepsSystemError() throws Exception {
        AdminTaskSubmission submission = service.submit(
                "TEST_EXECUTION_SYSTEM",
                "test:task:read",
                ignored -> {
                    throw new ExecutionException(new SystemException(
                            CommonErrorCode.SYSTEM_ERROR,
                            "internal details",
                            new IllegalStateException("cause")));
                });

        AdminTaskEvent event = awaitTerminal(submission);

        assertThat(event.errorCode()).isEqualTo(CommonErrorCode.SYSTEM_ERROR.getCode());
        assertThat(event.errorType()).isEqualTo("SYSTEM");
        assertThat(event.error()).isEqualTo(CommonErrorCode.SYSTEM_ERROR.getMessage());
    }

    @Test
    void unwrapsUndeclaredThrowableExceptionAndKeepsBusinessError() throws Exception {
        AdminTaskSubmission submission = service.submit(
                "TEST_UNDECLARED_BUSINESS",
                "test:task:read",
                ignored -> {
                    throw new UndeclaredThrowableException(
                            new BusinessException(409, "数据已变化"));
                });

        AdminTaskEvent event = awaitTerminal(submission);

        assertThat(event.errorCode()).isEqualTo(409);
        assertThat(event.errorType()).isEqualTo("BUSINESS");
        assertThat(event.error()).isEqualTo("数据已变化");
    }

    @Test
    void disconnectedClientCanRecoverCompletedResultByQueryAndResubscription() throws Exception {
        CountDownLatch finish = new CountDownLatch(1);
        AdminTaskSubmission submission = service.submit("REFRESH", "warframe:community:view",
                reporter -> {
                    assertThat(finish.await(3, TimeUnit.SECONDS)).isTrue();
                    return "preview-id";
                });
        var first = service.subscribe(submission.taskId(), ignored -> {});
        first.close();
        finish.countDown();
        var terminal = awaitTerminal(submission);
        assertThat(terminal.status()).isEqualTo(AdminTaskStatus.COMPLETED);
        assertThat(service.find(submission.taskId())).get()
                .extracting(AdminTaskEvent::result).isEqualTo("preview-id");
        AtomicReference<AdminTaskEvent> restored = new AtomicReference<>();
        try (var reconnect = service.subscribe(submission.taskId(), restored::set)) {
            assertThat(restored.get().status()).isEqualTo(AdminTaskStatus.COMPLETED);
            assertThat(restored.get().result()).isEqualTo("preview-id");
        }
    }

    @Test
    void removesFailingListenerWithoutInterruptingOtherSubscribers() throws Exception {
        CountDownLatch workStarted = new CountDownLatch(1);
        CountDownLatch publishProgress = new CountDownLatch(1);
        CountDownLatch terminal = new CountDownLatch(1);
        AtomicBoolean failListener = new AtomicBoolean();
        AtomicReference<AdminTaskEvent> latest = new AtomicReference<>();
        AdminTaskSubmission submission = service.submit(
                "TEST_LISTENER_ISOLATION",
                "test:task:read",
                reporter -> {
                    workStarted.countDown();
                    assertThat(publishProgress.await(3, TimeUnit.SECONDS)).isTrue();
                    reporter.report("PROCESSING", "处理中", 1, 2);
                    return "done";
                });

        assertThat(workStarted.await(3, TimeUnit.SECONDS)).isTrue();
        service.subscribe(submission.taskId(), event -> {
            if (failListener.get()) {
                throw new IllegalStateException("client disconnected");
            }
        });
        service.subscribe(submission.taskId(), event -> {
            latest.set(event);
            if (event.status() == AdminTaskStatus.COMPLETED) {
                terminal.countDown();
            }
        });

        failListener.set(true);
        publishProgress.countDown();

        assertThat(terminal.await(3, TimeUnit.SECONDS)).isTrue();
        assertThat(latest.get().status()).isEqualTo(AdminTaskStatus.COMPLETED);
        assertThat(latest.get().result()).isEqualTo("done");
    }

    private InMemoryAdminTaskService newService() {
        InMemoryAdminTaskService value = new InMemoryAdminTaskService();
        ReflectionTestUtils.setField(value, "executor", executor);
        return value;
    }

    private AdminTaskEvent awaitTerminal(AdminTaskSubmission submission) throws Exception {
        CountDownLatch terminal = new CountDownLatch(1);
        AtomicReference<AdminTaskEvent> latest = new AtomicReference<>();
        service.subscribe(submission.taskId(), event -> {
            latest.set(event);
            if (event.status() == AdminTaskStatus.COMPLETED
                    || event.status() == AdminTaskStatus.FAILED) {
                terminal.countDown();
            }
        });

        assertThat(terminal.await(3, TimeUnit.SECONDS)).isTrue();
        return latest.get();
    }
}
