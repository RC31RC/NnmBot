package com.nnm.bot.warframe;

import com.nnm.bot.warframe.persistent.curated.pojo.query.reference.WarframeCommunityQuery;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialChildRow;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialMainRepository;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateDisplayMapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class WarframePackageStructureTest {
    private static final Path ROOT = Path.of("src/main/java/com/nnm/bot/warframe");
    private static final Path OFFICIAL = ROOT.resolve("persistent/official");
    private static final Path CURATED = ROOT.resolve("persistent/curated");

    @Test
    void packagesFollowLifecycleAndSourceOwnership() throws IOException {
        for (String removed : List.of("shared", "rawdata", "parser", "service", "repository", "pojo",
                "cache", "client", "event", "mapper", "task", "listener", "messaging")) {
            assertThat(javaSources(ROOT.resolve(removed))).as(removed).isEmpty();
        }
        for (String rootPackage : List.of("plugin", "controller", "schedule", "mq", "storage")) {
            assertThat(javaSources(ROOT.resolve(rootPackage))).as(rootPackage).isNotEmpty();
        }
        assertThat(javaSources(ROOT.resolve("runtime/rawdata"))).isNotEmpty();
        assertThat(javaSources(ROOT.resolve("runtime/parser"))).isNotEmpty();
        assertThat(javaSources(OFFICIAL)).isNotEmpty();
        assertThat(javaSources(CURATED)).isNotEmpty();

        for (Path source : javaSources(ROOT)) {
            String relative = ROOT.relativize(source).toString();
            String text = Files.readString(source);
            String expected = "com.nnm.bot.warframe." + relative.substring(0, relative.lastIndexOf('/')).replace('/', '.');
            assertThat(text).as(source.toString()).contains("package " + expected + ";");
            assertThat(text).as(source.toString()).doesNotContain("wf_curated_", "wf_data_", "wf_catalog_", "wf_product_");
            if (text.contains("@Scheduled") || text.contains("@RdtScheduled")) {
                assertThat(relative).startsWith("schedule/");
            }
            if (text.contains("extends MessageDescriptor") || text.contains("@RdtQueueListener")) {
                assertThat(relative).startsWith("mq/");
            }
            if (text.contains("@Entity") && relative.startsWith("persistent/official/pojo/entity/")) {
                assertThat(text).containsPattern("@Table\\(\\s*name\\s*=\\s*\"wf_official_");
            }
        }
        assertThat(javaSources(ROOT.resolve("runtime/render/worldstate"))).isEmpty();
        assertThat(Files.isRegularFile(ROOT.resolve(
                "runtime/mapper/worldstate/WarframeWorldStateDisplayMapper.java"))).isTrue();
        assertThat(Files.exists(ROOT.resolve("runtime/event/worldstate/ChangeDetector.java"))).isFalse();
    }

    @Test
    void servicesAndPluginsRespectPersistenceBoundary() throws IOException {
        for (Path source : allServiceSources()) {
            assertThat(Files.readString(source)).as(source.toString()).doesNotContain(
                    "import com.querydsl.", "import jakarta.persistence.EntityManager;",
                    "import jakarta.persistence.PersistenceContext;", "import jakarta.persistence.criteria.",
                    "import com.nnm.bot.warframe.plugin.", "import com.nnm.bot.warframe.render.");
        }
        for (Path source : javaSources(ROOT.resolve("plugin"))) {
            assertThat(Files.readString(source)).as(source.toString()).doesNotContain(
                    "import com.nnm.bot.warframe.persistent.official.repository.",
                    "import com.nnm.bot.warframe.persistent.curated.repository.",
                    "import com.nnm.bot.warframe.persistent.official.pojo.entity.",
                    "import com.nnm.bot.warframe.persistent.curated.pojo.entity.");
        }
    }

    @Test
    void generatedChildrenHaveTypedParentsAndNoIndependentRepository() throws IOException {
        String repositories = String.join("\n", javaSources(OFFICIAL.resolve("repository")).stream()
                .map(path -> {
                    try {
                        return Files.readString(path);
                    } catch (IOException exception) {
                        throw new IllegalStateException(exception);
                    }
                }).toList());
        int count = 0;
        for (Path source : javaSources(OFFICIAL.resolve("pojo/entity"))) {
            String text = Files.readString(source);
            if (!text.contains("extends OfficialChildRow")) continue;
            count++;
            assertThat(text).contains("@ManyToOne(fetch = FetchType.LAZY, optional = false)",
                    "@JoinColumn(name = \"bkey\"", "ConstraintMode.NO_CONSTRAINT");
            String name = source.getFileName().toString().replace(".java", "");
            assertThat(Pattern.compile("(?:JpaRepository|OfficialMainRepository)<\\s*" + name + "\\b").matcher(repositories).find()).isFalse();
        }
        assertThat(count).isEqualTo(236);
    }

    @Test
    void everyJsonHasItsOwnMainEntityAndRepositoryWithoutATotalParentTable() throws IOException {
        JsonNode models = new ObjectMapper().readTree(Files.readString(
                Path.of("src/main/resources/warframe/official-models.json"))).path("models");
        int mainTables = 0;
        for (JsonNode model : models) {
            if (!model.path("parentTable").isNull()) continue;
            mainTables++;
            String type = model.path("className").asText();
            String category = model.path("packageName").asText().substring(
                    model.path("packageName").asText().lastIndexOf('.') + 1);
            Path entity = OFFICIAL.resolve("pojo/entity/" + category + "/" + type + ".java");
            assertThat(Files.readString(entity)).contains("extends OfficialRecord")
                    .doesNotContain("@JoinColumn(name = \"bkey\"", "@ManyToOne");
            String repository = type.replaceFirst("Root$", "") + "Repository";
            Path source = OFFICIAL.resolve("repository/" + category + "/" + repository + ".java");
            assertThat(Files.readString(source)).contains("extends OfficialMainRepository<" + type + ">",
                    "WarframeOfficialDataDomain." + model.path("domain").asText());
        }
        assertThat(mainTables).isEqualTo(71);
        try (Stream<Path> repositories = Files.list(OFFICIAL.resolve("repository"))) {
            assertThat(repositories.filter(path -> path.toString().endsWith(".java")))
                    .as("official repositories must be grouped by dataset or shared responsibility")
                    .isEmpty();
        }
        assertThat(Files.readString(OFFICIAL.resolve("pojo/entity/support/OfficialRecord.java")))
                .contains("@MappedSuperclass").doesNotContain("@Entity", "@Table", "@OneToMany");
        for (Path source : javaSources(ROOT)) {
            assertThat(Files.readString(source)).as(source.toString()).doesNotContain(
                    "wf_official_record", "WarframeOfficialRecordRepository", "QWarframeOfficialRecord");
        }
    }

    @Test
    void enumsAndTransferTypesStayInTheirOwnedPackages() throws IOException {
        for (Path source : javaSources(ROOT)) {
            if (Pattern.compile("\\benum\\s+[A-Za-z_$]").matcher(Files.readString(source)).find()) {
                assertThat("/" + ROOT.relativize(source)).as(source.toString()).contains("/constant/");
            }
        }
        assertThat(Files.isRegularFile(CURATED.resolve("pojo/query/reference/WarframeCommunityQuery.java"))).isTrue();
        for (String category : List.of("dto", "vo", "query")) {
            String suffix = switch (category) {
                case "dto" -> "DTO";
                case "vo" -> "VO";
                default -> "Query";
            };
            for (Path source : javaSources(ROOT.resolve("runtime/pojo/" + category))) assertTypesHaveSuffix(source, suffix);
            for (Path source : javaSources(OFFICIAL.resolve("pojo/" + category))) assertTypesHaveSuffix(source, suffix);
            for (Path source : javaSources(CURATED.resolve("pojo/" + category))) assertTypesHaveSuffix(source, suffix);
        }
        for (Path source : javaSources(ROOT.resolve("persistent"))) {
            if (source.toString().contains("/repository/")) {
                assertThat(Files.readString(source)).as(source.toString()).doesNotContain("pojo.vo.");
            }
        }
        for (Path source : javaSources(ROOT.resolve("controller"))) {
            assertThat(Files.readString(source)).as(source.toString())
                    .doesNotContainPattern("ApiResponse<[^(){};]*DTO");
        }
    }

    @Test
    void servicesContainOnlyContractsAndImplementations() throws IOException {
        for (Path source : allServiceSources()) {
            String text = Files.readString(source);
            assertThat(text).as(source.toString()).doesNotContain("@Entity")
                    .doesNotContainPattern("public (?:static )?(?:record|enum)\\s");
            if (!source.getParent().getFileName().toString().equals("impl")) {
                assertThat(text).as(source.toString()).contains("public interface ");
            }
        }
    }

    @Test
    void productionJsonBindingUsesExplicitModelsInsteadOfJsonTrees() throws IOException {
        for (Path source : javaSources(ROOT)) {
            assertThat(Files.readString(source)).as(source.toString())
                    .doesNotContain("JsonNode", "readTree(", "valueToTree(");
        }
    }

    private static List<Path> allServiceSources() throws IOException {
        return Stream.of(ROOT.resolve("runtime/service"), OFFICIAL.resolve("service"), CURATED.resolve("service"))
                .flatMap(path -> {
                    try {
                        return javaSources(path).stream();
                    } catch (IOException exception) {
                        throw new IllegalStateException(exception);
                    }
                }).toList();
    }

    private static void assertTypesHaveSuffix(Path source, String suffix) throws IOException {
        String text = Files.readString(source);
        var types = Pattern.compile("(?m)^\\s*(?:(?:public|private|protected|static|final)\\s+)*(?:class|record|interface)\\s+([A-Za-z_$][A-Za-z0-9_$]*)").matcher(text);
        while (types.find()) assertThat(types.group(1)).as(source.toString()).endsWith(suffix);
        assertThat(source.toString()).doesNotContain("/input/", "/output/");
    }

    private static List<Path> javaSources(Path directory) throws IOException {
        if (!Files.exists(directory)) return List.of();
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths.filter(path -> path.toString().endsWith(".java")).toList();
        }
    }
}
