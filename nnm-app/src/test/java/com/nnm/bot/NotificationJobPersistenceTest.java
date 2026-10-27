package com.nnm.bot;

import com.nnm.bot.admin.service.bot.notification.impl.BotNotificationStateServiceImpl;
import com.nnm.bot.core.constant.NotificationConstants.BlockType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import com.nnm.bot.admin.pojo.entity.bot.notification.BotNotificationJob;
import com.nnm.bot.admin.constant.BotNotificationConstants.JobStatus;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationDTO;
import com.nnm.bot.admin.repository.bot.notification.BotNotificationJobRepository;
import com.nnm.bot.admin.service.bot.notification.BotNotificationStateService;
import com.nnm.bot.core.mq.message.notification.NotificationContent;
import com.nnm.bot.core.mq.message.notification.NotificationResourceManifest;
import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;
import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionTemplate;
import static org.assertj.core.api.Assertions.*;

/** 真实 JPA/事务行锁验证；不启动应用、Redis、对象存储或任何发送器。 */
class NotificationJobPersistenceTest {
    @Test
    void jsonSnapshotsRoundTripAndTwoConcurrentTransactionsClaimOnlyOnce() throws Exception {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(new DriverManagerDataSource("jdbc:h2:mem:notification-jobs;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", ""));
        factory.setPackagesToScan("com.nnm.bot");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", "create-drop",
                "hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"));
        try {
            factory.afterPropertiesSet();
            EntityManagerFactory entityManagerFactory = factory.getObject();
            assertThat(entityManagerFactory).isNotNull();
            TransactionTemplate transactions = new TransactionTemplate(new JpaTransactionManager(entityManagerFactory));
            EntityManager entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
            BotNotificationJobRepository repository = new JpaRepositoryFactory(entityManager).getRepository(BotNotificationJobRepository.class);
            BotNotificationStateService store = new BotNotificationStateServiceImpl();
            ReflectionTestUtils.setField(store, "jobs", repository);
            UUID id = UUID.randomUUID();
            NotificationContent content = new NotificationContent(List.of(new NotificationContent.Message(List.of(
                    new NotificationContent.Block(BlockType.TEXT, "已编辑内容", null, null)), List.of(), null)),
                    CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT);
            BotGroupNotificationDTO request = new BotGroupNotificationDTO(UUID.randomUUID(), List.of(7L), content, Instant.ofEpochSecond(1800000000));
            BotNotificationJob job = new BotNotificationJob();
            job.setId(id); job.setOwnerId(42); job.setRequest(request);
            job.setResources(new NotificationResourceManifest(id, Map.of(), request.scheduledAt().plusSeconds(3600)));
            job.setScheduledAt(request.scheduledAt()); job.setStatus(JobStatus.SCHEDULED);
            job.setCreatedAt(Instant.now()); job.setUpdatedAt(Instant.now());
            transactions.executeWithoutResult(status -> entityManager.persist(job));
            BotNotificationJob loaded = transactions.execute(status -> repository.findById(id).orElseThrow());
            assertThat(loaded.getRequest()).isEqualTo(request);
            assertThat(loaded.getResources()).isEqualTo(job.getResources());
            CountDownLatch barrier = new CountDownLatch(2);
            AtomicInteger claimed = new AtomicInteger();
            try (ExecutorService workers = Executors.newFixedThreadPool(2)) {
                Future<?> first = workers.submit(() -> claim(transactions, store, id, barrier, claimed));
                Future<?> second = workers.submit(() -> claim(transactions, store, id, barrier, claimed));
                first.get(10, TimeUnit.SECONDS); second.get(10, TimeUnit.SECONDS);
            }
            assertThat(claimed).hasValue(1);
            JobStatus finalStatus = transactions.execute(status -> repository.findById(id).orElseThrow().getStatus());
            assertThat(finalStatus).isEqualTo(JobStatus.SUBMITTING);
        } finally {
            factory.destroy();
        }
    }

    private void claim(TransactionTemplate transactions, BotNotificationStateService store, UUID id,
                       CountDownLatch barrier, AtomicInteger claimed) {
        barrier.countDown();
        try {
            if (!barrier.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("Claim worker did not start");
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt(); throw new IllegalStateException(interrupted);
        }
        transactions.executeWithoutResult(status -> store.update(id, current -> {
            if (current.getStatus() == JobStatus.SCHEDULED) {
                current.setStatus(JobStatus.SUBMITTING); claimed.incrementAndGet();
            }
        }));
    }
}
