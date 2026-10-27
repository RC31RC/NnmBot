package com.nnm.bot.warframe;

import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFileDigestDTO;

import com.nnm.bot.warframe.persistent.official.parser.schema.WarframeOfficialModels;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialTableRelations;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialRecordSource;
import com.nnm.bot.warframe.persistent.official.service.record.impl.OfficialRecordPersistenceServiceImpl;

import com.nnm.bot.warframe.persistent.official.pojo.query.OfficialRecordIdentityQuery;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import com.nnm.bot.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.transaction.AfterCommitExecutor;
import com.nnm.bot.warframe.persistent.official.event.WarframeOfficialDataChangePublisher;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeWorldStateCodebookInvalidator;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportHead;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialFile;
import com.nnm.bot.warframe.persistent.official.pojo.entity.maintenance.WarframeOfficialRebuildMarker;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialFileRepository;
import com.nnm.bot.warframe.persistent.official.repository.maintenance.WarframeOfficialRebuildMarkerRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportHeadRepository;
import com.nnm.bot.warframe.persistent.official.service.record.OfficialRecordPersistenceService;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialRepositoryRegistry;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialMainRepository;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotParser;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialMaintenanceService;
import com.nnm.bot.warframe.persistent.official.service.dataset.impl.WarframeOfficialMaintenanceServiceImpl;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialImportRecordDTO;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApprovalService;
import com.nnm.bot.warframe.persistent.official.service.dataset.impl.WarframeOfficialApprovalServiceImpl;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialDetailService;
import io.github.rdt.queue.RdtMessageSendManager;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.time.Instant;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/** 专用 H2/JPA 事务测试，不启动应用、不访问 Redis 或外网。 */
@SpringJUnitConfig(OfficialImportApprovalTransactionTest.Config.class)
class OfficialImportApprovalTransactionTest {
    @Resource
    private WarframeOfficialApprovalService service;
    @Resource
    private WarframeOfficialMaintenanceService maintenance;
    @Resource
    private WarframeOfficialImportBatchRepository batches;
    @Resource
    private WarframeOfficialImportHeadRepository heads;
    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private OfficialRecordPersistenceService records;
    @Resource
    private OfficialRepositoryRegistry repositories;
    @Resource
    private WarframeOfficialFileRepository datasetFiles;
    @Resource
    private WarframeOfficialRebuildMarkerRepository rebuildMarkers;
    @Resource
    private WarframeOfficialDetailService details;
    @Resource
    private WarframeOfficialDataChangePublisher invalidator;
    @Resource
    private AtomicInteger committedInvalidations;
    @BeforeEach
    void resetDatabase() throws Exception {
        heads.deleteAllInBatch();
        batches.deleteAllInBatch();
        new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                repositories.all().forEach(repository -> records.deleteRecords(repository.findAll().stream()
                        .map(OfficialRecord::getId).toList())));
        rebuildMarkers.deleteAllInBatch();
        datasetFiles.deleteAllInBatch();
        WarframeOfficialImportHead head = new WarframeOfficialImportHead();
        head.setId(1);
        heads.saveAndFlush(head);
        reset(details, invalidator);
        committedInvalidations.set(0);
        doAnswer(invocation -> {
            new AfterCommitExecutor().execute(committedInvalidations::incrementAndGet);
            return null;
        }).when(invalidator).publishAfterCommit();
    }

    @Test
    void fingerprintUsesAdoptedSourceVersionButNotUnappliedCandidate() throws Exception {
        WarframeOfficialFile file = new WarframeOfficialFile();
        file.setProductPath("equipment/weapons.json");
        file.setAdoptedSha256("a".repeat(64));
        file.setCandidateSha256("a".repeat(64));
        file.setSourceRef("data-20261024");
        file.setCommitSha("1".repeat(40));
        file.setBuildId("test");
        file.setAdoptedAt(Instant.now());
        Method fingerprint = WarframeOfficialApprovalServiceImpl.class.getDeclaredMethod(
                "fingerprint", String.class, List.class);
        fingerprint.setAccessible(true);
        String baseline = (String) fingerprint.invoke(null, "root", List.of(file));
        file.setCandidateSha256("b".repeat(64));
        file.setJsonChanged(true);
        assertThat(fingerprint.invoke(null, "root", List.of(file))).isEqualTo(baseline);
        file.setAdoptedSha256("b".repeat(64));
        assertThat(fingerprint.invoke(null, "root", List.of(file))).isNotEqualTo(baseline);
    }

    @Test
    void commitsStableIdentityPreservesManualFieldsAndDoesNotDeleteOtherDatasets() throws Exception {
        OfficialRecord old = stored("WEAPON", "/Lotus/Existing", "old");
        old.setWikiSlug("手工页面");
        old.setMarketSlug("manual-market");
        old = records.saveAndFlush(old);
        Long stableId = old.getId();
        OfficialRecord removed = stored("WARFRAME", "/Lotus/Removed", "removed");

        removed = records.saveAndFlush(removed);
        OfficialRecord unsupported = stored("KEYS", "/Lotus/Unmapped", "keep");

        unsupported = records.saveAndFlush(unsupported);
        WarframeOfficialImportBatch batch = batch();

        service.apply(batch.getId(), (consumer -> List.of(incoming("/Lotus/Existing", "new"), incoming("/Lotus/New", "created")).forEach(consumer)),
                changedFiles());

        OfficialRecord updated = records.findById(stableId).orElseThrow();
        assertThat(updated.getLocalizedName()).isEqualTo("new");
        assertThat(updated.getWikiSlug()).isEqualTo("手工页面");
        assertThat(updated.getMarketSlug()).isEqualTo("manual-market");
        assertThat(records.findById(removed.getId()).orElseThrow().isEnabled()).isTrue();
        assertThat(records.findById(unsupported.getId()).orElseThrow().isEnabled()).isTrue();
        assertThat(batches.findById(batch.getId()).orElseThrow().getStatus()).isEqualTo("APPLIED");
        assertThat(heads.findById(1).orElseThrow().getBatchId()).isEqualTo(batch.getId());
        assertThat(committedInvalidations).hasValue(1);

        service.apply(batch.getId(), (consumer -> List.of(incoming("/Lotus/Existing", "must-not-replay")).forEach(consumer)), List.of());
        assertThat(repositories.all().stream().mapToLong(OfficialMainRepository::count).sum()).isEqualTo(4);
        assertThat(records.findById(stableId).orElseThrow().getLocalizedName()).isEqualTo("new");
        assertThat(committedInvalidations).hasValue(1);
    }

    @Test
    void detailFailureRollsBackRecordsBatchAndHeadWithoutInvalidatingCaches() throws Exception {
        OfficialRecord previous = records.saveAndFlush(stored("WEAPON", "/Lotus/Existing", "old"));
        WarframeOfficialImportBatch batch = batch();
        doThrow(new IllegalStateException("detail-write-failed")).when(details).synchronize(any(), any());

        assertThatThrownBy(() -> service.apply(batch.getId(), (consumer -> List.of(incoming("/Lotus/New", "new")).forEach(consumer)), changedFiles()))
                .isInstanceOf(IllegalStateException.class).hasMessage("detail-write-failed");

        assertThat(repositories.all().stream().mapToLong(OfficialMainRepository::count).sum()).isEqualTo(1);
        assertThat(records.findById(previous.getId()).orElseThrow().isEnabled()).isTrue();
        assertThat(batches.findById(batch.getId()).orElseThrow().getStatus()).isEqualTo("PENDING");
        assertThat(heads.findById(1).orElseThrow().getBatchId()).isNull();
        assertThat(committedInvalidations).hasValue(0);
    }

    @Test
    void rejectsAnOlderPreviewAfterAnotherBatchHasCommitted() throws Exception {
        WarframeOfficialImportBatch older = batch();
        WarframeOfficialImportBatch newer = batch();
        service.apply(newer.getId(), (consumer -> List.of(incoming("/Lotus/New", "new")).forEach(consumer)), changedFiles());

        assertThatThrownBy(() -> service.apply(older.getId(), (consumer -> List.<OfficialImportRecordDTO>of().forEach(consumer)), List.of()))
                .isInstanceOf(BusinessException.class).hasMessageContaining("基线");
        assertThat(heads.findById(1).orElseThrow().getBatchId()).isEqualTo(newer.getId());
        assertThat(repositories.all().stream().flatMap(repository -> repository.findSummaries().stream()).toList()).hasSize(1);
    }

    @Test
    void protectsManualChangesMadeAfterPreviewAndRejectsExpiredOrDifferentAdapter() throws Exception {
        OfficialRecord existing = records.saveAndFlush(stored("WEAPON", "/Lotus/Existing", "old"));
        WarframeOfficialImportBatch batch = batch();
        existing.setWikiSlug("edited-after-preview");
        records.saveAndFlush(existing);
        assertThatThrownBy(() -> service.apply(batch.getId(), (consumer -> List.<OfficialImportRecordDTO>of().forEach(consumer)), List.of()))
                .isInstanceOf(BusinessException.class).hasMessageContaining("预览后");
        WarframeOfficialImportBatch expired = batch();
        expired.setExpiresAt(Instant.now().minusSeconds(1));
        batches.saveAndFlush(expired);
        assertThatThrownBy(() -> service.apply(expired.getId(), (consumer -> List.<OfficialImportRecordDTO>of().forEach(consumer)), List.of())).isInstanceOf(BusinessException.class);
        WarframeOfficialImportBatch unsupported = batch();
        unsupported.setParserContractVersion(WarframeOfficialSnapshotParser.CONTRACT_VERSION + 1);
        batches.saveAndFlush(unsupported);
        assertThatThrownBy(() -> service.apply(unsupported.getId(), (consumer -> List.<OfficialImportRecordDTO>of().forEach(consumer)), List.of())).isInstanceOf(BusinessException.class);
        assertThat(committedInvalidations).hasValue(0);
    }

    @Test
    void fullValidEmptyDomainRemovesItsPreviousRecordsWithoutTouchingOtherDomains() throws Exception {
        OfficialRecord removed = records.saveAndFlush(stored("WEAPON", "/Lotus/Removed", "removed"));
        OfficialRecord unrelated = stored("KEYS", "/Lotus/Kept", "kept");

        OfficialRecord kept = records.saveAndFlush(unrelated);
        assertThat(changes( (consumer -> List.<OfficialImportRecordDTO>of().forEach(consumer)), java.util.Set.of("equipment/weapons.json"))).singleElement().satisfies(change -> {
            assertThat(change.changeType()).isEqualTo("REMOVED");
            assertThat(change.recordKey()).isEqualTo("/Lotus/Removed");
        });
        service.apply(batch().getId(), consumer -> {}, changedFiles());
        assertThat(records.findById(removed.getId())).isEmpty();
        assertThat(records.findById(kept.getId()).orElseThrow().isEnabled()).isTrue();
    }

    @Test
    void previewProvidesFrozenFieldLevelDiffsForChangedRoots() throws Exception {
        OfficialRecord current = stored("WEAPON", "/Lotus/Existing", "old");
        current.setRawJson("{\"name\":\"old\",\"masteryReq\":1}");
        current.setLocalizedRawJson(null);
        records.saveAndFlush(current);
        var changes = changes( (consumer -> List.of(new OfficialImportRecordDTO("equipment/weapons.json",
                WarframeOfficialDataDomain.WEAPON, "/Lotus/Existing", "new", "new", null, null, null,
                "{\"name\":\"new\",\"masteryReq\":2}", null)).forEach(consumer)),
                java.util.Set.of("equipment/weapons.json"));
        assertThat(changes).singleElement().satisfies(change -> {
            assertThat(change.changeType()).isEqualTo("CHANGED");
            assertThat(change.fieldChanges()).extracting(item -> item.pointer())
                    .containsExactly("/raw/masteryReq", "/raw/name");
        });
    }

    @Test
    void stagesCandidatesWithoutChangingAdoptedVersionOrApprovalBaseline() throws Exception {
        WarframeOfficialApprovalService fileApproval = service;
        String baseline = fileApproval.fingerprint();
        fileApproval.stageCandidates("local", "1".repeat(40), "build", changedFiles());
        WarframeOfficialFile state = datasetFiles.findById("equipment/weapons.json").orElseThrow();
        assertThat(state.getAdoptedSha256()).isNull();
        assertThat(state.getCandidateSha256()).isEqualTo("a".repeat(64));
        assertThat(state.isJsonChanged()).isTrue();
        assertThat(fileApproval.fingerprint()).isEqualTo(baseline);
    }

    @Test
    void unchangedHashWithoutRebuildSkipsRecordParsingAndDetailWrites() throws Exception {
        service.stageCandidates("local", "1".repeat(40), "build", changedFiles());
        WarframeOfficialFile adopted = datasetFiles.findById("equipment/weapons.json").orElseThrow();
        adopted.setAdoptedSha256("a".repeat(64));
        adopted.setParserContractVersion(WarframeOfficialSnapshotParser.CONTRACT_VERSION);
        adopted.setAdoptedAt(Instant.now());
        datasetFiles.saveAndFlush(adopted);
        assertThat(service.changedPaths(changedFiles())).isEmpty();
        service.apply(batch().getId(), consumer -> {
            throw new AssertionError("Unchanged files must never reach the record parser");
        }, changedFiles());
        org.mockito.Mockito.verifyNoInteractions(details);
    }

    @Test
    void unchangedHashStillRebuildsLocallyDeletedOfficialWithoutOverwritingSelfMaintained() throws Exception {
        OfficialRecord official = records.saveAndFlush(stored("WEAPON", "/Lotus/Existing", "old"));
        OfficialRecord self = stored("WEAPON", "/Lotus/Existing", "custom");
        self.setMaintenanceType(MaintenanceType.SELF_MAINTAINED);
        records.saveAndFlush(self);
        service.stageCandidates("local", "1".repeat(40), "build", changedFiles());
        WarframeOfficialFile state = datasetFiles.findById("equipment/weapons.json").orElseThrow();
        state.setAdoptedSha256("a".repeat(64));
        state.setParserContractVersion(WarframeOfficialSnapshotParser.CONTRACT_VERSION);
        state.setAdoptedAt(Instant.now());
        datasetFiles.saveAndFlush(state);
        records.deleteRecords(List.of(official.getId()));
        records.flush();
        WarframeOfficialRebuildMarker marker = new WarframeOfficialRebuildMarker();
        marker.setProductPath("equipment/weapons.json");
        marker.setDomainCode("WEAPON");
        marker.setBkey("/Lotus/Existing");
        marker.setRequestedAt(Instant.now());
        rebuildMarkers.saveAndFlush(marker);
        assertThat(service.changedPaths(changedFiles())).contains("equipment/weapons.json");
        service.apply(batch().getId(), (consumer -> List.of(incoming("/Lotus/Existing", "restored")).forEach(consumer)), changedFiles());
        assertThat(records.findById(official.getId())).isEmpty();
        assertThat(records.findForMaintenance(new OfficialRecordIdentityQuery(
                MaintenanceType.OFFICIAL, "WEAPON", "/Lotus/Existing"))
                .orElseThrow().getLocalizedName()).isEqualTo("restored");
        assertThat(records.findById(self.getId()).orElseThrow().getLocalizedName()).isEqualTo("custom");
        assertThat(rebuildMarkers.count()).isZero();
    }

    @Test
    void manualDeleteRemovesOfficialRootAndRegistersRebuildInSameTransaction() throws Exception {
        OfficialRecord official = records.saveAndFlush(stored("WEAPON", "/Lotus/Deleted", "deleted"));
        maintenance.delete(official.getId());
        assertThat(records.findById(official.getId())).isEmpty();
        assertThat(rebuildMarkers.findByProductPathAndDomainCodeAndBkey(
                "equipment/weapons.json", "WEAPON", "/Lotus/Deleted")).isPresent();
        assertThat(committedInvalidations).hasValue(1);
    }

    @Test
    void conversionNeedsSeparateConfirmationBeforeOverwritingSelfMaintainedTree() throws Exception {
        OfficialRecord official = records.saveAndFlush(stored("WEAPON", "/Lotus/Same", "official"));
        OfficialRecord self = stored("WEAPON", "/Lotus/Same", "custom");
        self.setMaintenanceType(MaintenanceType.SELF_MAINTAINED);
        self = records.saveAndFlush(self);
        assertThatThrownBy(() -> maintenance.convertToSelfMaintained(official.getId(), false, false))
                .isInstanceOf(BusinessException.class).hasMessageContaining("确认");
        assertThatThrownBy(() -> maintenance.convertToSelfMaintained(official.getId(), true, false))
                .isInstanceOf(BusinessException.class).hasMessageContaining("覆盖");
        assertThat(records.findById(self.getId()).orElseThrow().getLocalizedName()).isEqualTo("custom");
        assertThat(committedInvalidations).hasValue(0);
        WarframeOfficialRecordDTO converted = maintenance.convertToSelfMaintained(official.getId(), true, true);
        assertThat(converted.getId()).isEqualTo(self.getId());
        assertThat(converted.getLocalizedName()).isEqualTo("official");
        assertThat(records.findById(official.getId())).isPresent();
        assertThat(rebuildMarkers.count()).isZero();
        assertThat(committedInvalidations).hasValue(1);
    }

    @Test
    void failedManualDeletionRollsBackRootAndRebuildMarker() throws Exception {
        OfficialRecord official = records.saveAndFlush(stored("WEAPON", "/Lotus/Old", "old"));
        doThrow(new IllegalStateException("invalidation failed")).when(invalidator).publishAfterCommit();
        assertThatThrownBy(() -> maintenance.delete(official.getId()))
                .isInstanceOf(IllegalStateException.class).hasMessage("invalidation failed");
        assertThat(records.findById(official.getId())).isPresent();
        assertThat(rebuildMarkers.count()).isZero();
        assertThat(committedInvalidations).hasValue(0);
    }

    @Test
    void competingAdoptionsCannotBothCommitTheSameBaseline() throws Exception {
        WarframeOfficialImportBatch first = batch();
        WarframeOfficialImportBatch second = batch();
        CountDownLatch start = new CountDownLatch(1);
        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            List<Future<Boolean>> results = List.of(first, second).stream().map(batch -> pool.submit(() -> {
                start.await();
                try {
                    service.apply(batch.getId(), (consumer -> List.of(incoming("/Lotus/" + batch.getId(), "new")).forEach(consumer)), changedFiles());
                    return true;
                } catch (BusinessException expected) {
                    return false;
                }
            })).toList();
            start.countDown();
            int committed = 0;
            for (Future<Boolean> result : results) if (result.get(10, TimeUnit.SECONDS)) committed++;
            assertThat(committed).isEqualTo(1);
        }
        assertThat(repositories.all().stream().flatMap(repository -> repository.findSummaries().stream()).toList()).hasSize(1);
        assertThat(committedInvalidations).hasValue(1);
    }

    private static List<OfficialFileDigestDTO> changedFiles() {
        return List.of(new OfficialFileDigestDTO(
                "equipment/weapons.json", "a".repeat(64), 1L));
    }

    private WarframeOfficialImportBatch batch() {
        WarframeOfficialImportBatch batch = new WarframeOfficialImportBatch();
        batch.setId(UUID.randomUUID().toString());
        batch.setSourceRef("data-20261024");
        batch.setCommitSha("1".repeat(40));
        batch.setBuildId("test");
        batch.setManifestJson("{}");
        batch.setPreviewJson("{}");
        batch.setBaselineId(heads.findById(1).orElseThrow().getBatchId());
        batch.setBaselineHash(service.fingerprint());
        batch.setParserContractVersion(WarframeOfficialSnapshotParser.CONTRACT_VERSION);
        batch.setStatus("PENDING");
        batch.setCreatedAt(Instant.now());
        batch.setExpiresAt(Instant.now().plusSeconds(3600));
        return batches.saveAndFlush(batch);
    }

    private OfficialRecord stored(String type, String key, String name) {
        OfficialRecord record = records.newRecord(type);


        record.setBkey(key);
        record.setLocalizedName(name);
        record.setRawJson("{}");
        record.setLocalizedRawJson("{}");
        return record;
    }

    private static OfficialImportRecordDTO incoming(String key, String name) {
        return new OfficialImportRecordDTO("equipment/weapons.json", WarframeOfficialDataDomain.WEAPON,
                key, name, name, null, null, null, "{\"name\":\"" + name + "\"}", "{}");
    }

    private List<WarframeOfficialRecordChangeDTO> changes(
            WarframeOfficialRecordSource source, java.util.Set<String> paths)
            throws java.io.IOException {
        var result = new java.util.ArrayList<WarframeOfficialRecordChangeDTO>();
        service.changes(source, paths, result::add);
        return result;
    }

    @Configuration(proxyBeanMethods = false)
    @EnableTransactionManagement
    @Import({OfficialRepositoryRegistry.class, OfficialRecordPersistenceServiceImpl.class, OfficialTableRelations.class})
    @EnableJpaRepositories(basePackages = "com.nnm.bot.warframe.persistent.official.repository")
    static class Config {
        @Bean
        WarframeOfficialModels models(ObjectMapper mapper) throws Exception {
            return new WarframeOfficialModels();
        }

        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource("jdbc:h2:mem:official-approval;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "");
        }

        @Bean
        LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
            LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
            factory.setDataSource(dataSource);
            factory.setPackagesToScan("com.nnm.bot.warframe.persistent.official.pojo.entity");
            factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            factory.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", "create-drop",
                    "hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy.class.getName()));
            return factory;
        }

        @Bean
        PlatformTransactionManager transactionManager(EntityManagerFactory factory) {
            return new JpaTransactionManager(factory);
        }

        @Bean
        WarframeOfficialApprovalService approvalService() {
            return new WarframeOfficialApprovalServiceImpl();
        }

        @Bean
        WarframeOfficialMaintenanceService maintenanceService() {
            return new WarframeOfficialMaintenanceServiceImpl();
        }

        @Bean
        WarframeOfficialSnapshotParser snapshotParser() {
            return new WarframeOfficialSnapshotParser();
        }

        @Bean
        WarframeOfficialDetailService details() {
            return mock(WarframeOfficialDetailService.class);
        }

        @Bean
        WarframeOfficialDataChangePublisher invalidator() {
            return mock(WarframeOfficialDataChangePublisher.class);
        }

        @Bean
        WarframeWorldStateCodebookInvalidator codebookInvalidator() {
            return mock(WarframeWorldStateCodebookInvalidator.class);
        }

        @Bean
        WarframeWorldStateCodebookService codebook() {
            return mock(WarframeWorldStateCodebookService.class);
        }

        @Bean
        CacheManager cacheManager() {
            return mock(CacheManager.class);
        }

        @Bean
        RdtMessageSendManager messages() {
            return mock(RdtMessageSendManager.class);
        }

        @Bean
        AfterCommitExecutor afterCommitExecutor() {
            return new AfterCommitExecutor();
        }

        @Bean
        AtomicInteger committedInvalidations() {
            return new AtomicInteger();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
