package com.nnm.bot.warframe;

import com.nnm.bot.warframe.persistent.official.service.upgrades.impl.WarframeRivenWeaponServiceImpl;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialRepositoryRegistry;
import com.nnm.bot.warframe.persistent.official.service.record.impl.OfficialRecordPersistenceServiceImpl;

import com.nnm.bot.warframe.persistent.official.pojo.query.OfficialRecordIdentityQuery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.transaction.AfterCommitExecutor;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import io.github.rdt.queue.RdtMessageSendManager;
import org.springframework.cache.CacheManager;
import com.nnm.bot.warframe.persistent.official.event.WarframeOfficialDataChangePublisher;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeWorldStateCodebookInvalidator;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportHead;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportHeadRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialFileRepository;
import com.nnm.bot.warframe.persistent.official.service.record.OfficialRecordPersistenceService;
import com.nnm.bot.warframe.persistent.official.repository.maintenance.WarframeOfficialRebuildMarkerRepository;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotParser;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFileDigestDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialImportRecordDTO;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApprovalService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialMaintenanceService;
import com.nnm.bot.warframe.persistent.official.service.dataset.impl.WarframeOfficialApprovalServiceImpl;
import com.nnm.bot.warframe.persistent.official.service.dataset.impl.WarframeOfficialMaintenanceServiceImpl;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialDetailService;
import com.nnm.bot.warframe.persistent.official.service.record.impl.WarframeOfficialRecordReadServiceImpl;
import com.nnm.bot.warframe.persistent.official.service.record.impl.WarframeOfficialDetailServiceImpl;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeRivenWeaponService;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.nnm.bot.warframe.persistent.official.parser.schema.WarframeOfficialModels;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialTableRelations;
import com.nnm.bot.warframe.persistent.official.repository.upgrades.OfficialRivenWeaponRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@SpringJUnitConfig(OfficialRivenWeaponTreeTest.Config.class)
class OfficialRivenWeaponTreeTest {
    private static final String PRODUCT = "upgrades/riven-weapons.json";
    private static final String KEY = "/Lotus/Weapons/Example";
    @Resource
    private OfficialRecordPersistenceService records;
    @Resource
    private OfficialRivenWeaponRepository weapons;
    @Resource
    private OfficialTableRelations relations;
    @Resource
    private WarframeOfficialImportHeadRepository heads;
    @Resource
    private WarframeOfficialImportBatchRepository batches;
    @Resource
    private WarframeOfficialFileRepository files;
    @Resource
    private WarframeOfficialRebuildMarkerRepository rebuildMarkers;
    @Resource
    private WarframeOfficialDetailService details;
    @Resource
    private WarframeOfficialMaintenanceService maintenance;
    @Resource
    private WarframeOfficialApprovalService approval;
    @Resource
    private WarframeOfficialSnapshotParser snapshotParser;
    @Resource
    private WarframeRivenWeaponService catalog;
    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private ObjectMapper mapper;
    @PersistenceContext
    private EntityManager entityManager;
    @TempDir
    private Path directory;

    @BeforeEach
    void reset() {
        new TransactionTemplate(transactionManager).executeWithoutResult(ignored -> {
            records.deleteRecords(weapons.findAll().stream().map(OfficialRecord::getId).toList());
            files.deleteAllInBatch();
            rebuildMarkers.deleteAllInBatch();
            heads.deleteAllInBatch();
            batches.deleteAllInBatch();
            WarframeOfficialImportHead head = new WarframeOfficialImportHead();
            head.setId(1);
            heads.saveAndFlush(head);
        });
    }

    @Test
    void adoptsOneFileAndRoundTripsAllWeaponListingAndLocaleFieldsThroughRelations() throws Exception {
        String source = weapon("Official", "Official Alt", "market-1")
                .replace("\"name\":\"Official\"", "\"name\":\"/Language/NameKey\"")
                .replace("\"nameZh\":\"武器\"", "\"nameZh\":\"\"")
                .replace("\"zhName\":\"商品\"", "\"zhName\":\"\"");
        adopt(source, "a");
        OfficialRecord root = official();
        JsonNode tree = tree(root.getId());
        assertThat(tree.path("marketListings").get(0).path("id").asText()).isEqualTo("market-1");
        assertThat(tree.path("marketListings").get(0).path("zhName").asText()).isEmpty();
        JsonNode expected = mapper.readTree(source);
        JsonNode actual = mapper.valueToTree(details.loadAttributes(WarframeOfficialDataDomain.RIVEN_WEAPON,
                List.of(root.getId())).get(root.getId()));
        assertJson(actual, expected);
        assertThat(catalog.index().get("official alt").uniqueName()).isEqualTo(KEY);
        assertThat(files.findById(PRODUCT).orElseThrow().getParserContractVersion())
                .isEqualTo(WarframeOfficialSnapshotParser.CONTRACT_VERSION);
    }

    @Test
    void changedFileReplacesAllDescendantsAndRemovesOldAliasesAndLocales() throws Exception {
        adopt(weapon("Official", "Old Alias", "market-1"), "a");
        long rootId = official().getId();
        adopt(weapon("Official", "New Alias", "market-2"), "b");
        assertThat(official().getId()).isEqualTo(rootId);
        assertThat(tree(rootId).path("marketListings").get(0).path("id").asText()).isEqualTo("market-2");
        assertThat(catalog.index()).containsKey("new alias").doesNotContainKey("old alias");
        assertThat(count("OfficialRivenWeaponRoot")).isEqualTo(2);
        assertThat(count("OfficialRivenWeaponMarketListingsItems")).isEqualTo(1);
        assertThat(count("OfficialRivenWeaponMarketListingsItems")).isEqualTo(1);
    }

    @Test
    void olderParserContractRebuildsMissingRelationsEvenWhenHashAndRootJsonAreUnchanged() throws Exception {
        String source = weapon("Official", "Alias", "market-1");
        adopt(source, "a");
        long rootId = official().getId();
        new TransactionTemplate(transactionManager).executeWithoutResult(ignored -> {
            relations.deleteChildren("RIVEN_WEAPON", List.of(rootId));
            var file = files.findById(PRODUCT).orElseThrow();
            file.setParserContractVersion(WarframeOfficialSnapshotParser.CONTRACT_VERSION - 1);
            files.saveAndFlush(file);
        });
        assertThat(tree(rootId).path("marketListings")).isEmpty();
        adopt(source, "a");
        assertThat(official().getId()).isEqualTo(rootId);
        assertThat(tree(rootId).path("marketListings").get(0).path("id").asText()).isEqualTo("market-1");
        assertThat(files.findById(PRODUCT).orElseThrow().getParserContractVersion())
                .isEqualTo(WarframeOfficialSnapshotParser.CONTRACT_VERSION);
    }

    @Test
    void officialDeletionRebuildsSameHashAndSelfTreeKeepsItsOwnAliases() throws Exception {
        String original = weapon("Official", "Official Alias", "market-1");
        adopt(original, "a");
        long oldOfficialId = official().getId();
        var selfData = maintenance.convertToSelfMaintained(oldOfficialId, true, false);
        OfficialRecord self = records.findById(selfData.getId()).orElseThrow();
        assertThat(tree(oldOfficialId).isNull()).isFalse();
        assertThat(rebuildMarkers.count()).isZero();
        self.setOfficialName("Custom");
        self.setRawJson(weapon("Custom", "Custom Alias", "market-custom"));
        self.setLocalizedRawJson(self.getRawJson());
        new TransactionTemplate(transactionManager).executeWithoutResult(ignored -> {
            OfficialRecord saved = records.saveAndFlush(self);
            details.synchronize(List.of(saved), List.of());
        });
        adopt(original, "a");
        assertThat(weapons.count()).isEqualTo(3);
        assertThat(rebuildMarkers.count()).isZero();
        assertThat(catalog.index()).containsKey("custom alias").doesNotContainKey("official alias");
        long restoredId = official().getId();
        maintenance.delete(restoredId);
        assertThat(tree(restoredId).isNull()).isTrue();
        assertThat(rebuildMarkers.count()).isEqualTo(1);
        adopt(original, "a");
        assertThat(weapons.count()).isEqualTo(3);
        assertThat(catalog.index()).containsKey("custom alias").doesNotContainKey("official alias");
        maintenance.delete(official().getId());
        adopt(null, "b");
        assertThat(rebuildMarkers.count()).isZero();
        assertThat(weapons.findAll().stream().filter(item -> !item.getBkey().equals("$metadata"))).singleElement()
                .satisfies(item -> assertThat(item.getMaintenanceType()).isEqualTo(MaintenanceType.SELF_MAINTAINED));
    }

    @Test
    void unmodeledChildFieldRollsBackTheWholeFileAndKeepsOldTree() throws Exception {
        adopt(weapon("Official", "Old Alias", "market-1"), "a");
        long rootId = official().getId();
        String invalid = weapon("Changed", "New Alias", "market-2");
        var json = mapper.readTree(invalid);
        // 子行本地主键独立分配；回滚校验针对真实输入字段合同，不将源商品编号当作关系主键。
        ((ObjectNode) json.path("marketListings").get(0)).put("unexpectedField", "invalid");
        OfficialImportRecordDTO item = new OfficialImportRecordDTO(PRODUCT, WarframeOfficialDataDomain.RIVEN_WEAPON,
                KEY, "Changed", "武器", null, null, null, json.toString(), json.toString());
        String batch = batch();
        assertThatThrownBy(() -> approval.apply(batch, (consumer -> List.of(item).forEach(consumer)), payload("b")))
                .isInstanceOf(BusinessException.class).hasMessageContaining(PRODUCT + "#/marketListings/0/unexpectedField");
        assertThat(official().getId()).isEqualTo(rootId);
        assertThat(official().getOfficialName()).isEqualTo("Official");
        assertThat(tree(rootId).path("marketListings").get(0).path("id").asText()).isEqualTo("market-1");
        assertThat(catalog.index()).containsKey("old alias").doesNotContainKey("new alias");
        assertThat(files.findById(PRODUCT).orElseThrow().getAdoptedSha256()).isEqualTo("a".repeat(64));
        assertThat(batches.findById(batch).orElseThrow().getStatus()).isEqualTo("PENDING");
    }

    @Test
    void invalidStableChildIdentityRollsBackTheWholeFileAndKeepsOldTree() throws Exception {
        adopt(weapon("Official", "Old Alias", "market-1"), "a");
        long rootId = official().getId();
        String invalid = weapon("Changed", "New Alias", "market-2");
        var json = mapper.readTree(invalid);
        ((ArrayNode) json.path("marketListings"))
                .add(json.path("marketListings").get(0).deepCopy());
        OfficialImportRecordDTO item = new OfficialImportRecordDTO(PRODUCT, WarframeOfficialDataDomain.RIVEN_WEAPON,
                KEY, "Changed", "武器", null, null, null, json.toString(), json.toString());
        String batch = batch();
        assertThatThrownBy(() -> approval.apply(batch, (consumer -> List.of(item).forEach(consumer)), payload("b")))
                .isInstanceOf(BusinessException.class).hasMessageContaining("稳定 id");
        assertThat(official().getId()).isEqualTo(rootId);
        assertThat(official().getOfficialName()).isEqualTo("Official");
        assertThat(tree(rootId).path("marketListings").get(0).path("id").asText()).isEqualTo("market-1");
        assertThat(catalog.index()).containsKey("old alias").doesNotContainKey("new alias");
        assertThat(files.findById(PRODUCT).orElseThrow().getAdoptedSha256()).isEqualTo("a".repeat(64));
        assertThat(batches.findById(batch).orElseThrow().getStatus()).isEqualTo("PENDING");
    }

    @Test
    @EnabledIfSystemProperty(named = "nnm.test.dataset-directory", matches = ".+")
    void normalizesEveryReleasedWeaponAndListingWithoutRuntimeRawJsonParsing() throws Exception {
        Path source = Path.of(System.getProperty("nnm.test.dataset-directory"));
        JsonNode released = mapper.readTree(source.resolve(PRODUCT).toFile());
        List<OfficialImportRecordDTO> incoming = snapshotParser.readRecords(source, Set.of(PRODUCT));
        approval.apply(batch(), (consumer -> incoming.forEach(consumer)), payload("a"));
        assertThat(weapons.count()).isEqualTo(released.path("weapons").size() + 1);
        int listings = 0;
        for (JsonNode weapon : released.path("weapons")) listings += weapon.path("marketListings").size();
        assertThat(count("OfficialRivenWeaponMarketListingsItems")).isEqualTo(listings);
        List<OfficialRecord> roots = new java.util.ArrayList<>(weapons.findAll());
        Map<Long, Map<String, Object>> attributes = details.loadAttributes(WarframeOfficialDataDomain.RIVEN_WEAPON,
                roots.stream().map(OfficialRecord::getId).toList());
        Map<String, Long> rootIds = roots.stream().collect(Collectors.toMap(
                OfficialRecord::getBkey, OfficialRecord::getId));
        for (JsonNode weapon : released.path("weapons")) {
            ObjectNode expected = weapon.deepCopy();
            String key = expected.path("uniqueName").asText();
            JsonNode actual = mapper.valueToTree(attributes.get(rootIds.get(key)));
            assertThat(actual.equals((left, right) -> left.isNumber() && right.isNumber()
                    ? left.decimalValue().compareTo(right.decimalValue())
                    : left.equals(right) ? 0 : 1, expected))
                    .as("%s%nexpected: %s%nactual: %s", key, expected, actual).isTrue();
        }
        var statistics = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
        assertThat(catalog.index()).isNotEmpty();
        assertThat(statistics.getPrepareStatementCount()).isLessThanOrEqualTo(2L + 4L * ((roots.size() + 249) / 250));
    }

    private JsonNode tree(long rootId) {
        return mapper.valueToTree(records.loadDetails("RIVEN_WEAPON", List.of(rootId)).get(rootId));
    }

    private static void assertJson(JsonNode actual, JsonNode expected) {
        assertThat(actual.equals((left, right) -> left.isNumber() && right.isNumber()
                ? left.decimalValue().compareTo(right.decimalValue()) : left.equals(right) ? 0 : 1, expected)).isTrue();
    }

    private long count(String entity) {
        return entityManager.createQuery("select count(item) from " + entity + " item", Long.class)
                .getSingleResult();
    }

    private void adopt(String weapon, String hash) throws Exception {
        Path target = directory.resolve(PRODUCT);
        Files.createDirectories(target.getParent());
        Files.writeString(target, "{\"weapons\":[" + (weapon == null ? "" : weapon) + "]}");
        approval.apply(batch(), (consumer -> snapshotParser.readRecords(directory, Set.of(PRODUCT)).forEach(consumer)), payload(hash));
    }

    private List<OfficialFileDigestDTO> payload(String hash) {
        return List.of(new OfficialFileDigestDTO(PRODUCT, hash.repeat(64), null));
    }

    private OfficialRecord official() {
        return records.findForMaintenance(new OfficialRecordIdentityQuery(
                MaintenanceType.OFFICIAL, "RIVEN_WEAPON", KEY)).orElseThrow();
    }

    private String batch() {
        WarframeOfficialImportBatch batch = new WarframeOfficialImportBatch();
        batch.setId(UUID.randomUUID().toString());
        batch.setSourceRef("local");
        batch.setCommitSha("1".repeat(40));
        batch.setBuildId("riven-tree-test");
        batch.setManifestJson("{}");
        batch.setPreviewJson("{}");
        batch.setBaselineId(heads.findById(1).orElseThrow().getBatchId());
        batch.setBaselineHash(approval.fingerprint());
        batch.setParserContractVersion(WarframeOfficialSnapshotParser.CONTRACT_VERSION);
        batch.setStatus("PENDING");
        batch.setCreatedAt(Instant.now());
        batch.setExpiresAt(Instant.now().plusSeconds(3600));
        batches.saveAndFlush(batch);
        return batch.getId();
    }

    private static String weapon(String name, String alias, String marketId) {
        return """
                {"uniqueName":"%s","name":"%s","nameEn":"%s","nameZh":"武器",
                 "disposition":1.2,"iconUrl":"root-icon","iconStatus":"available","marketListings":[{
                   "id":"%s","slug":"example","gameRef":"%s","recordKey":"example","url_name":"example",
                   "name":"%s","nameEn":"%s","zhName":"商品","group":"rifle","rivenType":"rifle",
                   "disposition":1.1,"reqMasteryRank":8,"iconUrl":"listing-icon","iconStatus":"available",
                   "icon":"market-icon","thumb":"market-thumb"}]}
                """.formatted(KEY, name, name, marketId, KEY, alias, alias);
    }

    @Configuration
    @EnableTransactionManagement
    @Import({OfficialRepositoryRegistry.class, OfficialRecordPersistenceServiceImpl.class, WarframeOfficialModels.class, OfficialTableRelations.class})
    @EnableJpaRepositories(basePackages = "com.nnm.bot.warframe.persistent.official.repository")
    static class Config {
        @Bean
        DataSource dataSource() {
            String url = System.getProperty("nnm.test.postgres-url");
            if (url != null) {
                assertThat(url).matches("jdbc:postgresql://127\\.0\\.0\\.1:[0-9]+/nnmbot_sql_test");
                return new DriverManagerDataSource(url, "sql_test", "");
            }
            return new DriverManagerDataSource("jdbc:h2:mem:official_riven_tree;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "");
        }

        @Bean
        LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
            LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
            factory.setDataSource(dataSource);
            factory.setPackagesToScan("com.nnm.bot.warframe.persistent.official.pojo.entity");
            factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            boolean postgres = System.getProperty("nnm.test.postgres-url") != null;
            factory.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", postgres ? "validate" : "create-drop",
                    "hibernate.default_schema", postgres ? "nnmbot" : "PUBLIC",
                    "hibernate.generate_statistics", true,
                    "hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy.class.getName()));
            return factory;
        }

        @Bean
        PlatformTransactionManager transactionManager(EntityManagerFactory factory) {
            return new JpaTransactionManager(factory);
        }

        @Bean
        ObjectMapper mapper() {
            return new ObjectMapper();
        }

        @Bean
        WarframeOfficialSnapshotParser snapshotParser() {
            return new WarframeOfficialSnapshotParser();
        }

        @Bean
        WarframeOfficialApprovalService approval() {
            return new WarframeOfficialApprovalServiceImpl();
        }

        @Bean
        WarframeOfficialDetailService details() {
            return new WarframeOfficialDetailServiceImpl();
        }

        @Bean
        WarframeOfficialMaintenanceService maintenance() {
            return new WarframeOfficialMaintenanceServiceImpl();
        }

        @Bean
        WarframeOfficialRecordReadService catalogService() {
            return new WarframeOfficialRecordReadServiceImpl();
        }

        @Bean
        WarframeRivenWeaponService rivenWeaponCatalog() {
            return new WarframeRivenWeaponServiceImpl();
        }

        @Bean
        AfterCommitExecutor afterCommitExecutor() {
            return new AfterCommitExecutor();
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
        WarframeWorldStateCodebookService codebook() {
            return mock(WarframeWorldStateCodebookService.class);
        }

        @Bean
        WarframeOfficialDataChangePublisher itemInvalidator() {
            return mock(WarframeOfficialDataChangePublisher.class);
        }

        @Bean
        WarframeWorldStateCodebookInvalidator codebookInvalidator() {
            return mock(WarframeWorldStateCodebookInvalidator.class);
        }
    }
}
