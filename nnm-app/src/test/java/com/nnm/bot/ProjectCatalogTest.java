package com.nnm.bot;

import org.springframework.beans.factory.config.BeanDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityDataset;
import jakarta.annotation.Resource;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.ResolvableType;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Records declared dependencies, not a claim that every invocation reads every listed table.
 * Updating the catalog is explicit; ordinary test runs only check for drift.
 */
class ProjectCatalogTest {

    private static final List<String> MODULES = List.of(
            "nnm-core", "nnm-admin", "nnm-warframe", "nnm-render", "nnm-common", "nnm-app");
    private final Path root = Path.of(System.getProperty("maven.multiModuleProjectDirectory", ".."))
            .toAbsolutePath().normalize();

    @Test
    void catalogMatchesCompiledCommandsEntitiesAndSourceRegistry() throws Exception {
        List<Class<?>> commands = scan(new AnnotationTypeFilter(BotPluginCommand.class)).stream()
                .sorted(Comparator.comparing(type -> type.getAnnotation(BotPluginCommand.class).code()))
                .toList();
        List<Class<?>> entities = scan(new AnnotationTypeFilter(Entity.class));
        List<Class<?>> tableEntities = entities.stream()
                .filter(type -> type.getDeclaredAnnotation(Table.class) != null)
                .sorted(Comparator.comparing(type -> type.getDeclaredAnnotation(Table.class).name()))
                .toList();
        List<Class<?>> repositories = scan(new AssignableTypeFilter(Repository.class));
        List<Class<?>> services = scan(new AnnotationTypeFilter(Service.class));
        assertThat(commands).isNotEmpty();
        assertThat(commands.stream().map(type -> type.getAnnotation(BotPluginCommand.class).code()
                .strip().toLowerCase(Locale.ROOT))).doesNotHaveDuplicates();
        String semantics = Files.readString(root.resolve("docs/Warframe数据与命令实现.md"),
                StandardCharsets.UTF_8);
        for (Class<?> type : commands) {
            String code = type.getAnnotation(BotPluginCommand.class).code();
            assertThat(semantics).as("Missing command semantics: %s", code)
                    .contains("`" + code + "`");
        }

        StringBuilder document = new StringBuilder("""
                # 命令、数据表与来源目录

                本文件由 `ProjectCatalogTest` 从已编译的插件注解、JPA 元数据和来源登记生成。
                它列出代码能力，不表示命令已启用、远程数据已采纳或线上效果已验收。
                命令用途、参数和返回语义见 [Warframe 数据与命令实现](../Warframe数据与命令实现.md)。
                命令触发词、输出模式、机器人能力分配以管理端数据库配置为准，不从参考项目导入。

                """);
        document.append("统计：").append(commands.size()).append(" 个插件（Warframe ")
                .append(commands.stream().filter(type -> "warframe".equals(
                        type.getAnnotation(BotPluginCommand.class).gameCode())).count())
                .append(" 个），").append(entities.size()).append(" 张实体表，")
                .append(WarframeCommunityDataset.values().length).append(" 个人工白名单，")
                .append(WarframeOfficialDataDomain.values().length - 1).append(" 个官方文件。\n\n");
        document.append("""
                ## 命令

                下列 Service 是插件及其父类声明的直接注入依赖；不是逐请求调用追踪。
                `PREFIX` 接收参数，`EXACT` 为整条触发词匹配。

                | 稳定代码 | 名称 / 业务域 | 匹配 | 插件 | 直接 Service / Provider |
                | --- | --- | --- | --- | --- |
                """);
        List<CommandEntry> metadata = new ArrayList<>();
        for (Class<?> type : commands) {
            BotPluginCommand command = type.getAnnotation(BotPluginCommand.class);
            document.append("| `").append(command.code()).append("` | ")
                    .append(cell(command.name())).append(" / ").append(command.gameCode())
                    .append(" | ").append(command.matchType()).append(" | ").append(link(type))
                    .append(" | ").append(dependencies(type).stream().map(this::link)
                            .collect(Collectors.joining("<br>")))
                    .append(" |\n");
            metadata.add(new CommandEntry(command.code(), command.name(),
                    command.gameCode(), command.matchType().name()));
        }
        document.append("""

                ## 表与 Repository

                全部业务表位于 `nnmbot` schema。71 个官方 JSON 各有独立主表与 Repository，
                其必要子表只由所属主表维护，不建独立 Repository，也不建立跨 JSON 总父表。建表、字段语义和索引以 `sql/init.sql` 为准。

                | 表 | 实体 | Repository |
                | --- | --- | --- |
                """);
        for (Class<?> entity : tableEntities) {
            document.append("| `").append(entity.getDeclaredAnnotation(Table.class).name()).append("` | ")
                    .append(link(entity)).append(" | ")
                    .append(repositories.stream().filter(repository -> entity.equals(domain(repository)))
                            .map(this::link).collect(Collectors.joining("<br>")))
                    .append(" |\n");
        }
        document.append("""

                ## Service 与直接查询表

                这里只列 Service 自身及父类直接声明的 Repository；通过其他 Service 查询的表
                不展开，以免把共享解析依赖误写成每条命令必读。业务 Service 接口与实现仍分离。

                | Service 实现 | 直接 Repository / 表 |
                | --- | --- |
                """);
        for (Class<?> service : services) {
            List<Class<?>> direct = dependencies(service).stream()
                    .filter(Repository.class::isAssignableFrom).toList();
            if (direct.isEmpty()) {
                continue;
            }
            document.append("| ").append(link(service)).append(" | ")
                    .append(direct.stream().map(repository -> {
                        Class<?> entity = domain(repository);
                        Table table = table(entity);
                        return link(repository) + (table == null ? "" : " → `" + table.name() + "`");
                    }).collect(Collectors.joining("<br>"))).append(" |\n");
        }
        document.append("""

                ## 数据集、文件与项目来源

                官方领域按 manifest 相对路径建模，每个 JSON 的独立主表只关联自己的必要子表。
                人工输入仅保留四类白名单；WorldState、仲裁、周紫卡和 Market 订单分别走运行时通道，详见实现文档。

                | 数据集 / 用途 | 主表 | Repository | 文件或分区 | 来源类别 | 记录维护状态 |
                | --- | --- | --- | --- | --- | --- |
                """);
        var officialModels = new ObjectMapper().readTree(Files.readString(root.resolve(
                "nnm-warframe/src/main/resources/warframe/official-models.json")));
        for (WarframeOfficialDataDomain domain : WarframeOfficialDataDomain.values()) {
            if (domain == WarframeOfficialDataDomain.OTHER) continue;
            String mainTable = officialModels.path("roots").path(domain.domainCode()).asText();
            Class<?> mainEntity = tableEntities.stream()
                    .filter(entity -> entity.getDeclaredAnnotation(Table.class).name().equals(mainTable))
                    .findFirst().orElseThrow();
            List<Class<?>> mainRepositories = repositories.stream()
                    .filter(repository -> mainEntity.equals(domain(repository))).toList();
            assertThat(mainRepositories).as("One concrete repository for %s", domain.productPath()).hasSize(1);
            document.append("| `").append(domain.domainCode()).append("` | ")
                    .append('`').append(mainTable).append("` | ").append(link(mainRepositories.getFirst()))
                    .append(" | `").append(domain.productPath())
                    .append("` | official | OFFICIAL / SELF_MAINTAINED |\n");
        }
        for (WarframeCommunityDataset dataset : WarframeCommunityDataset.values()) {
            document.append("| `").append(dataset.code()).append("` | ")
                    .append(dataset.label()).append(" | 见表与 Repository 清单 | 人工白名单 | community | SELF_MAINTAINED |\n");
        }
        document.append("""

                ## 更新与校验

                更新目录及测试命令元数据：

                ```sh
                sh mvnw -pl nnm-app -am test -Dtest=ProjectCatalogTest \\
                  -Dsurefire.failIfNoSpecifiedTests=false -Dnnm.catalog.write=true
                ```

                普通测试检查目录与代码一致，不修改文档。数据库脚本另外校验全部插件都有命令配置，
                新增命令默认禁用，不覆盖已有设置；管理端配置触发词并分配机器人能力后再启用。
                """);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata) + "\n";
        verifyOrWrite(root.resolve("docs/reference/命令与数据目录.md"), document.toString());
        verifyOrWrite(root.resolve("nnm-app/src/test/resources/db/command-catalog.json"), json);
    }

    private List<Class<?>> scan(TypeFilter filter) throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent()
                        && !beanDefinition.getMetadata().isAnnotation();
            }
        };
        scanner.addIncludeFilter(filter);
        List<Class<?>> result = new ArrayList<>();
        for (BeanDefinition candidate : scanner.findCandidateComponents("com.nnm.bot")) {
            Class<?> type = Class.forName(candidate.getBeanClassName(), false, getClass().getClassLoader());
            if (source(type) != null) {
                result.add(type);
            }
        }
        return result.stream().sorted(Comparator.comparing(Class::getName)).toList();
    }

    private List<Class<?>> dependencies(Class<?> type) {
        TreeSet<Class<?>> result = new TreeSet<>(Comparator.comparing(Class::getName));
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            Arrays.stream(current.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Resource.class))
                    .map(Field::getType).filter(field -> source(field) != null).forEach(result::add);
        }
        return List.copyOf(result);
    }

    private Class<?> domain(Class<?> repository) {
        return ResolvableType.forClass(repository).as(Repository.class).getGeneric(0).resolve();
    }

    private static Table table(Class<?> type) {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            Table table = current.getDeclaredAnnotation(Table.class);
            if (table != null) {
                return table;
            }
        }
        return null;
    }

    private String source(Class<?> type) {
        for (String module : MODULES) {
            String file = module + "/src/main/java/" + type.getName().replace('.', '/') + ".java";
            if (Files.isRegularFile(root.resolve(file))) {
                return file;
            }
        }
        return null;
    }

    private String link(Class<?> type) {
        String file = source(type);
        return file == null ? type.getSimpleName() : "[" + type.getSimpleName() + "](../../" + file + ")";
    }

    private static String cell(String value) {
        return value.replace("|", "\\|").replace("\n", " ");
    }

    private void verifyOrWrite(Path path, String expected) throws Exception {
        if (Boolean.getBoolean("nnm.catalog.write")) {
            Files.createDirectories(path.getParent());
            Files.writeString(path, expected, StandardCharsets.UTF_8);
        }
        assertThat(Files.readString(path, StandardCharsets.UTF_8))
                .as("Catalog drift: run ProjectCatalogTest with -Dnnm.catalog.write=true: %s", path)
                .isEqualTo(expected);
    }

    private record CommandEntry(String code, String name, String gameCode, String matchType) {
    }
}
