package com.nnm.bot.warframe;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeOfficialWorldStateIndex;
import com.nnm.bot.warframe.persistent.official.config.worldstate.WarframeWorldStateDataProperties;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotParser;
import com.nnm.bot.warframe.persistent.official.parser.schema.WarframeOfficialModels.RowModel;
import com.nnm.bot.warframe.persistent.official.parser.schema.WarframeOfficialModels.SchemaField;
import com.nnm.bot.warframe.persistent.official.parser.schema.WarframeOfficialModels;
import com.nnm.bot.warframe.plugin.persistent.official.rewards.RelicsPlugin;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialRelicDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.OfficialRelicStatesEntries;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.OfficialRelicStatesEntriesRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialChildRow;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRow;
import com.nnm.bot.warframe.persistent.official.pojo.query.OfficialRecordQuery;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRelicVO;
import com.nnm.bot.warframe.persistent.official.repository.rewards.OfficialRelicRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportHeadRepository;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialMainRepository;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialRepositoryRegistry;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialTableRelations;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeRelicService;
import com.nnm.bot.warframe.persistent.official.service.rewards.impl.WarframeRelicServiceImpl;
import com.nnm.bot.warframe.persistent.official.service.image.WarframeImageResolver;
import com.nnm.bot.warframe.persistent.official.service.record.OfficialRecordPersistenceService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.record.impl.OfficialRecordPersistenceServiceImpl;
import com.nnm.bot.warframe.persistent.official.service.record.impl.WarframeOfficialRecordReadServiceImpl;
import com.nnm.bot.warframe.persistent.official.service.record.impl.WarframeOfficialSelectionServiceImpl;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(OfficialModelTreeTest.Config.class)
class OfficialModelTreeTest {
    @Resource
    private OfficialRecordPersistenceService roots;
    @Resource
    private OfficialRelicRepository relics;
    @Resource
    private WarframeOfficialImportHeadRepository heads;
    @Resource
    private OfficialRepositoryRegistry repositories;
    @Resource
    private WarframeOfficialModels models;
    @Resource
    private WarframeOfficialSnapshotParser snapshotParser;
    @Resource
    private WarframeOfficialRecordReadService runtimeRecords;
    @Resource
    private WarframeRelicService relicService;
    @Resource
    private ObjectMapper mapper;
    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private ApplicationContext context;
    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void clearIndependentOfficialTables() {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            repositories.all().forEach(repository ->
                    roots.deleteRecords(repository.findAll().stream().map(OfficialRecord::getId).toList()));
            heads.findById(1).ifPresent(head -> {
                head.setBatchId(null);
                heads.saveAndFlush(head);
            });
        });
    }

    @Test
    void independentMainRepositoriesHaveMatchingModelsAndReturnDetachedProjections() {
        assertThat(repositories.all()).hasSize(71);
        repositories.all().forEach(repository -> assertThat(repository.newRecord().getClass())
                .isEqualTo(models.root(repository.domain().domainCode()).type()));
        OfficialRecord weapon = root("WEAPON", "independent-test", "{\"name\":\"Weapon\"}");
        OfficialRecord relic = root("RELIC", "independent-test", "{\"name\":\"Relic\"}");
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        try {
            transaction.executeWithoutResult(status -> {
                roots.saveAndFlush(weapon);
                roots.saveAndFlush(relic);
                roots.synchronizeTables(List.of(weapon, relic), List.of());
                var projected = repositories.require(WarframeOfficialDataDomain.WEAPON)
                        .findDataById(weapon.getId()).orElseThrow();
                projected.setBkey("must-not-persist");
                roots.flush();
            });
            assertThat(weapon.getId()).isNotEqualTo(relic.getId());
            assertThat(repositories.require(WarframeOfficialDataDomain.WEAPON).findDataById(weapon.getId()))
                    .get().extracting(WarframeOfficialRecordDTO::getBkey)
                    .isEqualTo("independent-test");
            assertThat(repositories.require(WarframeOfficialDataDomain.RELIC).findDataById(weapon.getId())).isEmpty();
            assertThat(repositories.require(WarframeOfficialDataDomain.RELIC).findDataById(relic.getId())).isPresent();
            assertThat(repositories.require(WarframeOfficialDataDomain.WEAPON).findPage(
                    new OfficialRecordQuery("WEAPON", "independent-test", "OFFICIAL",
                            PageRequest.of(0, 20, Sort.by(
                                    Sort.Order.asc("localizedName").nullsLast(),
                                    Sort.Order.asc("id"))))).getContent())
                    .singleElement().extracting(WarframeOfficialRecordDTO::getId).isEqualTo(weapon.getId());
        } finally {
            transaction.executeWithoutResult(status -> roots.deleteRecords(List.of(weapon.getId(), relic.getId())));
        }
    }

    @Test
    void preservesDeepTreesEmptyStringsPrecisionAndDictionaryKeysWithoutReadingImportText() throws Exception {
        String json = """
                {"uniqueName":"/Lotus/Weapons/Test","name":"/Lotus/Language/Weapon",
                 "nameZh":"","disposition":1.1234567890123456789,"marketListings":[
                 {"id":"first","name":"","zhName":"","reqMasteryRank":8,"icon":"","thumb":"thumb"},
                 {"id":"second","nameEn":"Alias","zhName":""}]}
                """;
        OfficialRecord root = root("RIVEN_WEAPON", "tree", json);
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            roots.saveAndFlush(root);
            roots.synchronizeTables(List.of(root), List.of());
            root.setRawJson("this import text must never be parsed by readers");
            roots.saveAndFlush(root);
        });
        JsonNode expected = read(json);
        assertJson(roots.loadDetails("RIVEN_WEAPON", List.of(root.getId())).get(root.getId()), expected);
        RowModel detail = models.root("RIVEN_WEAPON");
        OfficialRow parent = entityManager.createQuery("from " + detail.type().getSimpleName()
                + " where id = :id", OfficialRow.class).setParameter("id", root.getId()).getSingleResult();
        RowModel listing = models.table(detail.definition().children().stream()
                .filter(table -> table.contains("marketlistings")).findFirst().orElseThrow());
        List<OfficialChildRow> listings = entityManager.createQuery("from " + listing.type().getSimpleName()
                + " where bkey = :id order by sortOrder", OfficialChildRow.class)
                .setParameter("id", parent.getId()).getResultList();
        assertThat(listings).hasSize(2).allSatisfy(row -> assertThat(row.getBkey()).isEqualTo(parent.getId()));
        assertThat(listings).extracting(OfficialChildRow::getSortOrder).containsExactly(0, 1);
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            roots.deleteRecords(List.of(root.getId()));
        });
        assertThat(roots.loadDetails("RIVEN_WEAPON", List.of(root.getId()))).isEmpty();
        assertThat(entityManager.createQuery("select count(row) from " + listing.type().getSimpleName()
                + " row where bkey = :id", Long.class).setParameter("id", parent.getId()).getSingleResult()).isZero();
    }

    @Test
    void rejectsUnknownNestedFieldsWithFileAndPointerBeforeReplacingExistingTree() throws Exception {
        OfficialRecord root = root("RIVEN_WEAPON", "invalid", "{\"name\":\"old\",\"marketListings\":[]}");
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            roots.saveAndFlush(root);
            roots.synchronizeTables(List.of(root), List.of());
        });
        JsonNode original = mapper.valueToTree(roots.loadDetails("RIVEN_WEAPON", List.of(root.getId())).get(root.getId()));
        root.setRawJson("{\"marketListings\":[{\"id\":\"x\",\"unexpected\":true}]}");
        assertThatThrownBy(() -> new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                roots.synchronizeTables(List.of(root), List.of())))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("upgrades/riven-weapons.json#/marketListings/0/unexpected");
        assertJson(roots.loadDetails("RIVEN_WEAPON", List.of(root.getId())).get(root.getId()), original);
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            roots.deleteRecords(List.of(root.getId()));
        });
    }

    @Test
    void relicGrandchildFilterPagesRootsAndHonorsOverridesWithoutOrphans() throws Exception {
        String template = "{\"uniqueName\":\"%s\",\"states\":{\"Intact\":{\"rewards\":["
                + "{\"rewardName\":\"%s\",\"item__uniqueName\":\"%s\"},"
                + "{\"rewardName\":\"%s\",\"item__uniqueName\":\"%s\"}]}}}";
        List<OfficialRecord> records = new ArrayList<>();
        records.add(root("RELIC", "A", template.formatted("A", "/reward", "/reward", "/reward", "/reward")));
        records.add(root("RELIC", "B", template.formatted("B", "/reward", "/reward", "/other", "/other")));
        records.add(root("RELIC", "C", template.formatted("C", "/reward", "/reward", "/other", "/other")));
        var override = root("RELIC", "C", template.formatted("C", "/custom", "/custom", "/custom", "/custom"));
        override.setMaintenanceType(MaintenanceType.SELF_MAINTAINED);
        records.add(override);
        var tx = new TransactionTemplate(transactionManager);
        tx.executeWithoutResult(status -> {
            records.forEach(roots::save);
            roots.flush();
            roots.synchronizeTables(records, List.of());
        });
        try {
            tx.executeWithoutResult(status -> {
                var states = entityManager.createQuery("from OfficialRelicStatesEntries where bkey = :id",
                                OfficialRelicStatesEntries.class).setParameter("id", records.getFirst().getId()).getResultList();
                assertThat(states).singleElement().satisfies(state -> {
                    assertThat(state.getBkey()).isEqualTo(records.getFirst().getId());
                    assertThat(state.getParent().getId()).isEqualTo(state.getBkey());
                    var rewards = entityManager.createQuery("from OfficialRelicStatesEntriesRewardsItems where bkey = :id",
                                    OfficialRelicStatesEntriesRewardsItems.class).setParameter("id", state.getId()).getResultList();
                    assertThat(rewards).hasSize(2).allSatisfy(reward -> {
                        // 孙表关联直接父行，不跳过状态表指向最上层遗物记录。
                        assertThat(reward.getBkey()).isEqualTo(state.getId()).isNotEqualTo(records.getFirst().getId());
                        assertThat(reward.getParent().getId()).isEqualTo(reward.getBkey());
                    });
                });
            });
            assertThat(relics.findPreferredRelicIds("/reward"))
                    .containsExactly(records.get(0).getId(), records.get(1).getId());
            assertThat(relics.findPreferredRelicIds("/custom")).containsExactly(override.getId());
            var page = relics.findPage(new OfficialRecordQuery(
                    "RELIC", "", "SELF_MAINTAINED", PageRequest.of(0, 10)));
            assertThat(page.getContent()).singleElement().satisfies(summary -> {
                assertThat(summary.getId()).isEqualTo(override.getId());
                assertThat(summary.getRawJson()).isNull();
            });
            var restored = roots.loadDetails("RELIC", List.of(override.getId()));
            assertJson(restored.get(override.getId()), read(override.getRawJson()));
        } finally {
            tx.executeWithoutResult(status -> {
                roots.deleteRecords(records.stream().map(OfficialRecord::getId).toList());
            });
        }
        entityManager.getMetamodel().getEntities().stream()
                .filter(type -> type.getName().startsWith("OfficialRelic"))
                .forEach(type -> assertThat(entityManager.createQuery("select count(r) from " + type.getName()
                        + " r", Long.class).getSingleResult()).as(type.getName()).isZero());
    }

    @Test
    @EnabledIfSystemProperty(named = "nnm.test.dataset-directory", matches = ".+")
    void validatesEveryManifestFieldAndRoundTripsAllObservedFieldShapesFromTypedTables() throws Exception {
        Path source = Path.of(System.getProperty("nnm.test.dataset-directory"));
        List<OfficialRecord> samples = new ArrayList<>();
        Map<String, JsonNode> expected = new LinkedHashMap<>();
        Set<String> covered = new HashSet<>();
        Set<String> domains = new HashSet<>();
        AtomicInteger total = new AtomicInteger();
        snapshotParser.forEachRecord(source, Set.copyOf(snapshotParser.supportedPaths()), imported -> {
            try {
                JsonNode value = read(imported.rawJson());
                String domain = imported.domain().domainCode();
                models.validate(domain, imported.rawJson());
                Set<String> fields = new HashSet<>();
                features(models.root(domain), models.root(domain).definition().fields(), value, fields);
                if (!covered.containsAll(fields)) {
                    covered.addAll(fields);
                    OfficialRecord root = root(domain, imported.recordKey(), imported.rawJson());

                    samples.add(root);
                    expected.put(domain + ":" + root.getBkey(), value);
                }
                domains.add(domain);
                total.incrementAndGet();
            } catch (Exception exception) {
                throw new IllegalStateException(imported.path() + "#" + imported.recordKey(), exception);
            }
        });
        assertThat(domains).containsExactlyInAnyOrderElementsOf(snapshotParser.supportedDomainCodes());
        assertThat(total.get()).isGreaterThan(400_000);
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            samples.forEach(roots::save);
            roots.flush();
            roots.synchronizeTables(samples, List.of());
        });
        for (String domain : domains) {
            List<OfficialRecord> selected = samples.stream()
                    .filter(root -> models.domain(root.getClass()).domainCode().equals(domain)).toList();
            Map<Long, OfficialRecordDTO> restored = roots.loadDetails(domain,
                    selected.stream().map(OfficialRecord::getId).toList());
            for (var root : selected) assertJson(restored.get(root.getId()), expected.get(domain + ":" + root.getBkey()));
        }
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            roots.deleteRecords(samples.stream().map(OfficialRecord::getId).toList());
        });
        assertThat(repositories.all().stream().mapToLong(OfficialMainRepository::count).sum()).isZero();
        System.out.println("Official contract: records=" + total.get() + ", domains=" + domains.size()
                + ", field shapes=" + covered.size() + ", representative trees=" + samples.size());
    }

    @Test
    void runtimeQueriesPreferTheWholeSelfMaintainedTreeAndDoNotParseAuditJson() {
        OfficialRecord official = root("RELIC", "priority-relic", """
                {"name":"Official Relic","states":{"Intact":{"rewards":[
                  {"rewardName":"Official Reward","rarity":"Rare","probability":0.02}]}},
                 "locations":[{"source":"Official Location","probability":0.1}]}
                """);
        OfficialRecord self = root("RELIC", "PRIORITY-RELIC", """
                {"name":"Self Relic","states":{
                   "Intact":{"rewards":[{"rewardName":"Self Reward","rarity":"Rare","probability":0.02}]},
                   "Radiant":{"rewards":[{"rewardName":"Self Reward","rarity":"Rare","probability":0.1}]}},
                 "locations":[{"source":"Self Location","probability":0.2}]}
                """);
        self.setMaintenanceType(MaintenanceType.SELF_MAINTAINED);
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        try {
            tx.executeWithoutResult(status -> {
                roots.saveAndFlush(official);
                roots.saveAndFlush(self);
                roots.synchronizeTables(List.of(official, self), List.of());
                self.setRawJson("audit text is deliberately unavailable to runtime queries");
                roots.saveAndFlush(self);
            });
            var statistics = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
            statistics.clear();
            assertThat(relics.findPreferredRelicIds("Official Reward")).isEmpty();
            assertThat(relics.findPreferredRelicIds("Self Reward")).containsExactly(self.getId());
            var selected = runtimeRecords.record(WarframeOfficialDataDomain.RELIC, "priority-relic").orElseThrow();
            assertThat(selected.databaseId()).isEqualTo(self.getId());
            assertThat(((OfficialRelicDTO) selected.data()).getStates())
                    .containsOnlyKeys("Intact", "Radiant");
            assertThat(relicService.findDetails("Self Reward")).hasSize(2).allSatisfy(detail ->
                    assertThat(detail.drops()).singleElement().satisfies(drop ->
                            assertThat(drop.locationText()).isEqualTo("Self Location")));
            RelicsPlugin plugin = new RelicsPlugin();
            ReflectionTestUtils.setField(plugin, "relicService", relicService);
            ReflectionTestUtils.setField(plugin, "imageResolver", mock(WarframeImageResolver.class));
            var result = plugin.handle(new CommandInvocation(mock(BotMessageContext.class), "relics", "遗物", "Self Reward"));
            assertThat(result.successful()).isTrue();
            WarframeRelicVO view = result.replies().stream().flatMap(reply -> reply.blocks().stream())
                    .filter(ReplyBlock.DataView.class::isInstance).map(block -> ((ReplyBlock.DataView<?>) block).data())
                    .map(WarframeRelicVO.class::cast).findFirst().orElseThrow();
            assertThat(view.items()).isNotEmpty().allSatisfy(item -> {
                assertThat(item.rewards()).singleElement().satisfies(reward ->
                        assertThat(reward.rewardName()).isEqualTo("Self Reward"));
                assertThat(item.drops()).singleElement().satisfies(drop ->
                        assertThat(drop.location()).isEqualTo("Self Location"));
            });
            assertThat(statistics.getEntityLoadCount()).isZero();
            tx.executeWithoutResult(status -> {
                self.setEnabled(false);
                roots.saveAndFlush(self);
            });
            assertThat(runtimeRecords.record(WarframeOfficialDataDomain.RELIC, "priority-relic").orElseThrow().databaseId())
                    .isEqualTo(official.getId());
        } finally {
            tx.executeWithoutResult(status -> roots.deleteRecords(List.of(official.getId(), self.getId())));
        }
    }

    @Test
    void dynamicCodebookUsesSelfMaintenanceBeforeFirstImportAndRestoresOfficialWhenDisabled() {
        OfficialRecord self = root("MISSION_TYPE", "MT_EXTERMINATION", """
                {"name":"EXTERMINATE","nameEn":"Custom Exterminate","nameZh":"自维护歼灭"}
                """);
        self.setMaintenanceType(MaintenanceType.SELF_MAINTAINED);
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.executeWithoutResult(status -> {
            roots.saveAndFlush(self);
            roots.synchronizeTables(List.of(self), List.of());
            self.setRawJson("audit JSON must not be parsed by dynamic codebooks");
            roots.saveAndFlush(self);
        });
        assertThat(runtimeRecords.currentBatchId()).isEmpty();
        WarframeOfficialWorldStateIndex index = new WarframeOfficialWorldStateIndex();
        ReflectionTestUtils.setField(index, "catalog", runtimeRecords);
        ReflectionTestUtils.setField(index, "codebookProperties", new WarframeWorldStateDataProperties());
        var statistics = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
        var snapshot = index.snapshot();
        assertThat(snapshot.codebooks().get(WarframeReferenceType.MISSION_TYPE).get("mt_extermination").displayName())
                .isEqualTo("自维护歼灭");
        assertThat(statistics.getEntityLoadCount()).isZero();

        OfficialRecord official = root("MISSION_TYPE", "mt_extermination", """
                {"name":"EXTERMINATE","nameEn":"Official Exterminate","nameZh":"官方歼灭"}
                """);
        tx.executeWithoutResult(status -> {
            roots.saveAndFlush(official);
            roots.synchronizeTables(List.of(official), List.of());
        });
        index.reload();
        assertThat(index.snapshot().codebooks().get(WarframeReferenceType.MISSION_TYPE).get("mt_extermination").displayName())
                .isEqualTo("自维护歼灭");
        tx.executeWithoutResult(status -> {
            self.setEnabled(false);
            roots.saveAndFlush(self);
        });
        index.reload();
        assertThat(index.snapshot().codebooks().get(WarframeReferenceType.MISSION_TYPE).get("mt_extermination").displayName())
                .isEqualTo("官方歼灭");
    }

    @Test
    @EnabledIfSystemProperty(named = "nnm.test.postgres-url", matches = ".+")
    void runtimeReadKeepsOneCompleteTreeWhenApprovalCommitsBetweenSummaryAndDetails() {
        // PostgreSQL 的 REPEATABLE READ 使用事务级 MVCC 快照；H2 对新增子行的隔离行为不同。
        OfficialRecord record = root("RELIC", "concurrent-relic", """
                {"name":"Before","states":{"Intact":{"rewards":[{"rewardName":"Before Reward"}]}}}
                """);
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.executeWithoutResult(status -> {
            roots.saveAndFlush(record);
            roots.synchronizeTables(List.of(record), List.of());
        });
        OfficialRelicRepository intercepted = mock(OfficialRelicRepository.class);
        when(intercepted.findSummaries()).thenAnswer(invocation -> {
            var summaries = relics.findSummaries();
            // 独立线程提交新树，确保发生在读请求的摘要查询和详情查询之间。
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                executor.submit(() -> tx.executeWithoutResult(status -> {
                    record.setRawJson("""
                            {"name":"After","states":{"Radiant":{"rewards":[{"rewardName":"After Reward"}]}}}
                            """);
                    roots.saveAndFlush(record);
                    roots.synchronizeTables(List.of(record), List.of());
                })).get(10, TimeUnit.SECONDS);
            }
            return summaries;
        });
        OfficialRepositoryRegistry controlled = mock(OfficialRepositoryRegistry.class);
        doReturn(intercepted).when(controlled).require(WarframeOfficialDataDomain.RELIC);
        // 通过 Spring 创建独立的事务代理，测试拦截器不会改动其他测试共享的读取服务。
        WarframeOfficialRecordReadService reader = context.getAutowireCapableBeanFactory()
                .createBean(WarframeOfficialRecordReadServiceImpl.class);
        ReflectionTestUtils.setField(reader, "repositories", controlled);
        var before = (OfficialRelicDTO) reader.records(WarframeOfficialDataDomain.RELIC).getFirst().data();
        assertThat(before.getName()).isEqualTo("Before");
        assertThat(before.getStates()).containsOnlyKeys("Intact");
        assertThat(before.getStates().get("Intact").getRewards()).singleElement().satisfies(reward ->
                assertThat(reward.getRewardName()).isEqualTo("Before Reward"));
        var after = (OfficialRelicDTO) runtimeRecords.record(WarframeOfficialDataDomain.RELIC, "concurrent-relic")
                .orElseThrow().data();
        assertThat(after.getName()).isEqualTo("After");
        assertThat(after.getStates()).containsOnlyKeys("Radiant");
    }

    private void features(RowModel model, SchemaField field, JsonNode value, Set<String> features) {
        if (value == null) return;
        features.add(model.definition().table() + ":" + field.state() + ":" + value.getNodeType()
                + (value.isTextual() && value.textValue().isEmpty() ? ":empty" : ""));
        if (field.relation() != null && (value.isArray() || Boolean.TRUE.equals(field.dictionary()))) {
            RowModel child = models.table(field.relation());
            for (JsonNode member : value) features(child, child.definition().fields(), member, features);
        } else if (value.isObject()) {
            for (SchemaField child : field.children()) features(model, child, value.get(child.path().getLast()), features);
        }
    }

    private static void assertJson(OfficialRecordDTO record, JsonNode expected) {
        JsonNode actual = new ObjectMapper().valueToTree(record);
        assertThat(actual.equals((left, right) -> left.isNumber() && right.isNumber()
                ? left.decimalValue().compareTo(right.decimalValue()) : left.equals(right) ? 0 : 1, expected))
                .as("expected %s but received %s", expected, actual).isTrue();
    }

    private JsonNode read(String json) throws Exception {
        return mapper.reader().with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS).readTree(json);
    }

    private OfficialRecord root(String domain, String key, String json) {
        OfficialRecord root = (OfficialRecord) models.root(domain).create();

        root.setBkey(key);

        root.setRawJson(json);
        root.setEnabled(true);
        return root;
    }

    @Configuration
    @EnableTransactionManagement
    @EnableJpaRepositories(basePackages = "com.nnm.bot.warframe.persistent.official.repository")
    @Import({OfficialRepositoryRegistry.class, OfficialRecordPersistenceServiceImpl.class, WarframeOfficialModels.class, OfficialTableRelations.class, WarframeOfficialSnapshotParser.class,
            WarframeOfficialRecordReadServiceImpl.class, WarframeOfficialSelectionServiceImpl.class,
            WarframeRelicServiceImpl.class})
    static class Config {
        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        DataSource dataSource() {
            String url = System.getProperty("nnm.test.postgres-url");
            if (url != null) {
                assertThat(url).matches("jdbc:postgresql://127\\.0\\.0\\.1:[0-9]+/nnmbot_sql_test");
                return new DriverManagerDataSource(url, "sql_test", "");
            }
            return new DriverManagerDataSource("jdbc:h2:mem:official-model;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "");
        }

        @Bean
        LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
            var factory = new LocalContainerEntityManagerFactoryBean();
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
        PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
        }
    }
}
