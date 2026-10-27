package com.nnm.bot.warframe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.warframe.persistent.curated.cache.reference.WarframeItemIndexInvalidator;
import com.nnm.bot.warframe.persistent.curated.client.reference.WarframeCommunityInputClient;
import com.nnm.bot.warframe.persistent.curated.config.reference.WarframeCommunityProperties;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityDatasetLock;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityApplyDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityPreviewDTO;
import com.nnm.bot.warframe.persistent.curated.parser.reference.WarframeCommunityImportParser;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityAliasRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityApplyReceiptRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityDatasetLockRepository;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityDataService;
import com.nnm.bot.warframe.persistent.curated.service.reference.impl.WarframeCommunityDataServiceImpl;
import io.github.rdt.state.SharedStateStore;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** 用真实 JPA 事务验证选择采纳、跨批回滚和幂等，不接触应用数据库。 */
@SpringJUnitConfig(CommunityDataTransactionTest.Config.class)
class CommunityDataTransactionTest {
    @Resource
    private WarframeCommunityDataService service;
    @Resource
    private WarframeCommunityAliasRepository aliases;
    @Resource
    private WarframeCommunityApplyReceiptRepository receipts;
    @Resource
    private WarframeCommunityDatasetLockRepository locks;
    @Resource
    private ObjectMapper mapper;
    @Resource
    private SharedStateStore state;
    @Resource
    private WarframeCommunityInputClient client;
    private final Map<String, String> previews = new ConcurrentHashMap<>();

    @BeforeEach
    void resetDatabase() {
        aliases.deleteAllInBatch();
        receipts.deleteAllInBatch();
        for (WarframeCommunityDataset dataset : WarframeCommunityDataset.values()) {
            var row = new WarframeCommunityDatasetLock();
            row.setDataset(dataset.code());
            locks.saveAndFlush(row);
        }
        reset(state, client);
        previews.clear();
        when(state.get(anyString(), eq(String.class))).thenAnswer(call -> Optional.ofNullable(previews.get(call.getArgument(0))));
        doAnswer(call -> {
            previews.put(call.getArgument(0), call.getArgument(1));
            return null;
        }).when(state).put(anyString(), any(), any(Duration.class));
    }

    @Test
    void onlySelectedOperationsWriteAndSameSelectionRetryIsIdempotent() {
        WarframeCommunityPreviewDTO preview = service.preview("alias", "[{\"aliasName\":\"母牛\",\"targetName\":\"Hildryn\"},"
                + "{\"aliasName\":\"摸尸\",\"targetName\":\"Nekros\"}]");
        assertThat(aliases.count()).isZero();
        String selected = preview.operations().getFirst().id();
        var request = new WarframeCommunityApplyDTO(preview.id(), Set.of(selected));
        assertThat(service.apply("alias", request).appliedCount()).isOne();
        assertThat(aliases.count()).isOne();
        assertThat(service.apply("alias", request).appliedCount()).isOne();
        assertThat(aliases.count()).isOne();
        assertThatThrownBy(() -> service.apply("alias", new WarframeCommunityApplyDTO(preview.id(),
                preview.operations().stream().map(operation -> operation.id()).collect(Collectors.toSet()))))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void absentInputNeverProposesOrAppliesDeletion() {
        applyAll(service.preview("alias", "{\"aliasName\":\"母牛\",\"targetName\":\"Hildryn\"}"));
        WarframeCommunityPreviewDTO empty = service.preview("alias", "[]");
        assertThat(empty.operations()).isEmpty();
        assertThat(aliases.count()).isOne();
        assertThatThrownBy(() -> service.apply("alias", new WarframeCommunityApplyDTO(empty.id(), Set.of())))
                .isInstanceOf(BusinessException.class);
        service.delete("alias", aliases.findAll().getFirst().getId());
        assertThat(aliases.count()).isZero();
    }

    @Test
    void conflictAfterFirstFlushRollsBackTheWholeSelectedSet() throws Exception {
        var initial = IntStream.range(0, 251).mapToObj(index ->
                Map.of("aliasName", "name" + index, "targetName", "before")).toList();
        applyAll(service.preview("alias", mapper.writeValueAsString(initial)));
        var incoming = initial.stream().map(row -> Map.of("aliasName", row.get("aliasName"), "targetName", "after")).toList();
        WarframeCommunityPreviewDTO pending = service.preview("alias", mapper.writeValueAsString(incoming));
        var last = aliases.findAllByRecordKeyIn(Set.of("name250")).getFirst();
        last.setTargetName("concurrent");
        last.setUpdatedAt(Instant.now());
        aliases.saveAndFlush(last);
        assertThatThrownBy(() -> applyAll(pending)).isInstanceOf(BusinessException.class);
        assertThat(aliases.findAllByRecordKeyIn(Set.of("name0")).getFirst().getTargetName()).isEqualTo("before");
        assertThat(aliases.findAllByRecordKeyIn(Set.of("name250")).getFirst().getTargetName()).isEqualTo("concurrent");
        assertThat(receipts.findById(pending.id())).isEmpty();
    }

    @Test
    void uploadAndPullShareTheSameCanonicalPreviewAndCannotWriteAutomatically() {
        String json = "{\"aliasName\":\"母牛\",\"targetName\":\"Hildryn\"}";
        WarframeCommunityPreviewDTO manual = service.preview("alias", json);
        var upload = new MockMultipartFile("file", "aliases.json", "application/json", json.getBytes(StandardCharsets.UTF_8));
        WarframeCommunityPreviewDTO uploaded = service.uploadPreview("alias", upload);
        assertThat(uploaded.operations().getFirst().value()).isEqualTo(manual.operations().getFirst().value());
        assertThatThrownBy(() -> service.pullPreview("alias")).isInstanceOf(BusinessException.class);
        when(client.validate("https://example.test/aliases.json")).thenReturn(java.net.URI.create("https://example.test/aliases.json"));
        when(client.fetch("https://example.test/aliases.json")).thenReturn(json);
        service.source("alias", "https://example.test/aliases.json");
        assertThat(service.pullPreview("alias").operations().getFirst().value()).isEqualTo(uploaded.operations().getFirst().value());
        assertThat(aliases.count()).isZero();
    }

    @Test
    void rejectsUnknownFieldsDuplicateIdentitiesAndUnselectedIds() {
        for (String json : new String[]{"{\"aliasName\":\"x\",\"targetName\":\"y\",\"sourceCode\":\"remote\"}",
                "[{\"aliasName\":\"x\",\"targetName\":\"a\"},{\"aliasName\":\"x\",\"targetName\":\"b\"}]"}) {
            assertThatThrownBy(() -> service.preview("alias", json)).isInstanceOf(BusinessException.class);
        }
        WarframeCommunityPreviewDTO pending = service.preview("alias", "{\"aliasName\":\"x\",\"targetName\":\"a\"}");
        assertThatThrownBy(() -> service.apply("alias", new WarframeCommunityApplyDTO(pending.id(), Set.of("unknown"))))
                .isInstanceOf(BusinessException.class);
        assertThat(aliases.count()).isZero();
    }

    @Test
    void acceptsOfficialSlashSlugsAndOneToThreeAttributeCombinations() {
        WarframeCommunityPreviewDTO preview = service.preview("riven_alias", "{\"aliasName\":\"基双爆\","
                + "\"attributeCode\":\"base_damage_/_melee_damage+critical_chance+critical_damage\"}");
        assertThat(preview.operations()).hasSize(1);
        assertThatThrownBy(() -> service.preview("riven_alias", "{\"aliasName\":\"four\","
                + "\"attributeCode\":\"a+b+c+d\"}")).isInstanceOf(BusinessException.class);
    }

    private void applyAll(WarframeCommunityPreviewDTO preview) {
        service.apply(preview.dataset(), new WarframeCommunityApplyDTO(preview.id(), preview.operations().stream()
                .map(operation -> operation.id()).collect(Collectors.toSet())));
    }

    @Configuration
    @Import(WarframeCommunityImportParser.class)
    @EnableTransactionManagement
    @EnableJpaRepositories(basePackageClasses = WarframeCommunityAliasRepository.class)
    static class Config {
        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource("jdbc:h2:mem:community;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "");
        }

        @Bean
        LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource source) {
            var factory = new LocalContainerEntityManagerFactoryBean();
            factory.setDataSource(source);
            factory.setPackagesToScan("com.nnm.bot.warframe.persistent.curated.pojo.entity.reference");
            factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            factory.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", "create-drop", "hibernate.physical_naming_strategy",
                    CamelCaseToUnderscoresNamingStrategy.class.getName()));
            return factory;
        }

        @Bean
        PlatformTransactionManager transactionManager(EntityManagerFactory factory) {
            return new JpaTransactionManager(factory);
        }

        @Bean
        WarframeCommunityDataService community() {
            return new WarframeCommunityDataServiceImpl();
        }

        @Bean
        ObjectMapper mapper() {
            return new ObjectMapper().findAndRegisterModules();
        }

        @Bean
        WarframeCommunityProperties properties() {
            return new WarframeCommunityProperties();
        }

        @Bean
        WarframeCommunityInputClient client() {
            return mock(WarframeCommunityInputClient.class);
        }

        @Bean
        SharedStateStore state() {
            return mock(SharedStateStore.class);
        }

        @Bean
        org.springframework.cache.CacheManager cacheManager() {
            return mock(org.springframework.cache.CacheManager.class);
        }

        @Bean
        io.github.rdt.queue.RdtMessageSendManager messages() {
            return mock(io.github.rdt.queue.RdtMessageSendManager.class);
        }

        @Bean
        com.nnm.bot.common.transaction.AfterCommitExecutor afterCommit() {
            return new com.nnm.bot.common.transaction.AfterCommitExecutor();
        }

        @Bean
        WarframeItemIndexInvalidator invalidator() {
            return mock(WarframeItemIndexInvalidator.class);
        }
    }
}
