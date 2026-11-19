package com.nnm.bot.warframe.persistent.official.parser;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest.FileEntry;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.common.error.ErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.OfficialSnapshotErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.OfficialSnapshotProtocol;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Map;
import java.util.Set;

/** 发布契约校验；完整路径决定业务身份，大文件只做流式语法检查。 */
@Component
public class WarframeOfficialSnapshotValidator {

    private static final Set<String> DIRECTORIES = Set.of("equipment", "upgrades", "items", "crafting", "world",
        "rewards", "progression", "worldstate", "market", "reference", "statistics");
    private static final Set<String> REQUIRED_PATHS = Set.copyOf(WarframeOfficialDataDomain.paths());
    private static final Set<String> ARRAY_PATHS = Set.of("equipment/abilities.json.gz", "equipment/archwings.json.gz",
        "equipment/railjack-weapons.json.gz", "equipment/warframes.json.gz", "equipment/weapons.json.gz",
        "market/trade-items.json.gz", "statistics/usage-2020.json.gz", "statistics/usage-2021.json.gz",
        "statistics/usage-2022.json.gz", "statistics/usage-2023.json.gz", "statistics/usage-2024.json.gz",
        "statistics/usage-2025.json.gz", "upgrades/arcanes.json.gz", "upgrades/avionics.json.gz",
        "upgrades/focus-upgrades.json.gz", "upgrades/mod-sets.json.gz", "upgrades/mods.json.gz",
        "upgrades/riven-attributes.json.gz", "upgrades/riven-definitions.json.gz");
    private static final Map<String,
        Map<String, JsonToken>> WRAPPERS = Map.of("world/star-chart.json.gz", Map.of("systems", JsonToken.START_ARRAY),
            "rewards/relics.json.gz", Map.of("relics", JsonToken.START_ARRAY), "rewards/bounties.json.gz",
            Map.of("definitions", JsonToken.START_OBJECT, "rewardPools", JsonToken.START_OBJECT),
            "worldstate/activities.json.gz",
            Map.of("conquests__types", JsonToken.START_OBJECT, "conquests__conditions", JsonToken.START_OBJECT,
                "conquests__personalModifiers", JsonToken.START_OBJECT, "conquests__missionVariants",
                JsonToken.START_OBJECT, "descents__challenges", JsonToken.START_OBJECT, "descents__gameModes",
                JsonToken.START_OBJECT, "descents__protoframes", JsonToken.START_OBJECT),
            // 裂罅武器目录消费 weapons；该目录不含运行时周紫卡统计。
            "upgrades/riven-weapons.json.gz", Map.of("weapons", JsonToken.START_ARRAY));

    private static final Map<String, Set<String>> COUNT_PATHS = Map.of("world/star-chart.json.gz",
        Set.of("/systems/*/nodes"), "rewards/relics.json.gz", Set.of("/relics"), "rewards/bounties.json.gz",
        Set.of("/definitions", "/rewardPools/*"), "rewards/drop-tables.json.gz", Set.of("/contexts"),
        "worldstate/activities.json.gz",
        Set.of("/conquests__types", "/conquests__conditions", "/conquests__personalModifiers",
            "/conquests__missionVariants/*", "/descents__challenges", "/descents__gameModes", "/descents__protoframes"),
        "upgrades/riven-weapons.json.gz", Set.of("/weapons"));

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeOfficialImportProperties properties;

    /**
     * 读取并校验官方数据发布清单。
     *
     * @param path 压缩发布清单文件。
     * @return 游戏资料数据文件发布清单对象。
     */
    public WarframeOfficialManifest manifest(Path path) {
        try (JsonParser parser = strictParser(path)) {
            WarframeOfficialManifest manifest = objectMapper.readValue(parser, WarframeOfficialManifest.class);
            check(parser.nextToken() == null, OfficialSnapshotErrorCode.MANIFEST_TRAILING_JSON);
            check(manifest != null, OfficialSnapshotErrorCode.MANIFEST_EMPTY);
            check(manifest.schemaVersion() == OfficialSnapshotProtocol.SCHEMA_VERSION,
                OfficialSnapshotErrorCode.MANIFEST_VERSION_UNSUPPORTED);
            check(OfficialSnapshotProtocol.PRODUCER.equals(manifest.producer()),
                OfficialSnapshotErrorCode.MANIFEST_PRODUCER_INVALID);
            check(
                manifest.buildId() != null
                    && OfficialSnapshotProtocol.BUILD_ID_PATTERN.matcher(manifest.buildId()).matches(),
                OfficialSnapshotErrorCode.MANIFEST_BUILD_ID_INVALID);
            check(manifest.createdAt() != null, OfficialSnapshotErrorCode.MANIFEST_CREATED_AT_MISSING);
            check(CollUtil.isNotEmpty(manifest.files()) && manifest.files().size() <= properties.getMaxFiles(),
                OfficialSnapshotErrorCode.MANIFEST_FILE_COUNT_INVALID);
            Set<String> paths = new HashSet<>();
            for (FileEntry entry : manifest.files()) {
                check(entry != null, OfficialSnapshotErrorCode.MANIFEST_FILE_EMPTY);
                validatePath(entry.path());
                check(REQUIRED_PATHS.contains(entry.path()), OfficialSnapshotErrorCode.FILE_UNREGISTERED);
                check(paths.add(entry.path()), OfficialSnapshotErrorCode.FILE_PATH_DUPLICATE);
                check(entry.sha256() != null && entry.sha256().matches("[a-f0-9]{64}"),
                    OfficialSnapshotErrorCode.FILE_DIGEST_INVALID);
                check(entry.bytes() > 0 && entry.bytes() <= properties.getMaxFileBytes(),
                    OfficialSnapshotErrorCode.FILE_SIZE_LIMIT);
                check(OfficialSnapshotProtocol.GZIP_MEDIA_TYPE.equals(entry.mediaType()),
                    OfficialSnapshotErrorCode.FILE_MEDIA_TYPE_INVALID);
                check(entry.recordCount() == null || entry.recordCount() >= 0,
                    OfficialSnapshotErrorCode.FILE_RECORD_COUNT_INVALID);
            }
            Set<String> missing = new HashSet<>(REQUIRED_PATHS);
            missing.removeAll(paths);
            check(CollUtil.isEmpty(missing), OfficialSnapshotErrorCode.MANIFEST_REQUIRED_FILES_MISSING);
            return manifest;
        } catch (IOException | IllegalArgumentException exception) {
            throw new BusinessException(OfficialSnapshotErrorCode.MANIFEST_INVALID, exception);
        }
    }

    /**
     * 核验单个官方文件的路径、大小与摘要，拒绝清单以外或已损坏的输入。
     *
     * @param entry 条目。
     * @param path 当前文件或业务资源的路径。
     */
    public void file(FileEntry entry, Path path) {
        try {
            check(Files.isRegularFile(path) && Files.size(path) == entry.bytes()
                && entry.bytes() <= properties.getMaxFileBytes(), OfficialSnapshotErrorCode.FILE_SIZE_MISMATCH);
            MessageDigest digest = sha256();
            try (InputStream input = new DigestInputStream(Files.newInputStream(path), digest)) {
                input.transferTo(OutputStream.nullOutputStream());
            }
            check(HexFormat.of().formatHex(digest.digest()).equals(entry.sha256()),
                OfficialSnapshotErrorCode.FILE_DIGEST_MISMATCH);
            try (InputStream compressed = Files.newInputStream(path);
                InputStream input = new GZIPInputStream(compressed);
                JsonParser parser = objectMapper.getFactory().createParser(input)) {
                parser.enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION.mappedFeature());
                JsonToken root = parser.nextToken();
                boolean expectedArray = ARRAY_PATHS.contains(entry.path());
                check(root == (expectedArray ? JsonToken.START_ARRAY : JsonToken.START_OBJECT),
                    OfficialSnapshotErrorCode.FILE_ROOT_INVALID);
                Map<String, JsonToken> required = WRAPPERS.getOrDefault(entry.path(), Map.of());
                Set<String> seen = new HashSet<>();
                Set<String> countedPaths = COUNT_PATHS.getOrDefault(entry.path(), Set.of());
                long count = 0;
                while (parser.nextToken() != (expectedArray ? JsonToken.END_ARRAY : JsonToken.END_OBJECT)) {
                    check(parser.currentToken() != null, OfficialSnapshotErrorCode.FILE_TRUNCATED);
                    if (parser.currentToken() == JsonToken.FIELD_NAME) {
                        String field = parser.currentName();
                        JsonToken value = parser.nextToken();
                        if (required.containsKey(field)) {
                            check(required.get(field) == value, OfficialSnapshotErrorCode.FILE_WRAPPER_FIELD_INVALID);
                            seen.add(field);
                        }
                        count += "rewards/drop-tables.json.gz".equals(entry.path()) && "contexts".equals(field)
                            ? countRewards(parser)
                            : CollUtil.isEmpty(countedPaths) ? 1 : count(parser, "/" + field, countedPaths);
                    } else {
                        count++;
                    }
                    parser.skipChildren();
                }
                check(entry.recordCount() == null || entry.recordCount() == count,
                    OfficialSnapshotErrorCode.FILE_RECORD_COUNT_MISMATCH);
                check(seen.containsAll(required.keySet()), OfficialSnapshotErrorCode.FILE_WRAPPER_MISSING);
                check(parser.nextToken() == null, OfficialSnapshotErrorCode.FILE_TRAILING_JSON);
            }
        } catch (IOException exception) {
            throw new BusinessException(OfficialSnapshotErrorCode.FILE_READ_FAILED, exception);
        }
    }

    /**
     * 包装文件按契约统计业务集合；星图跨星系累加节点，不能拿顶层字段数替代。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param pointer 用于定位错误或差异的协议字段路径。
     * @param targets 目标集合。
     * @return 当前范围内符合条件的条目数。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private long count(JsonParser parser, String pointer, Set<String> targets) throws IOException {
        boolean counted = targets.contains(pointer);
        if (!counted && targets.stream().noneMatch(target -> target.startsWith(pointer + "/"))) {
            parser.skipChildren();
            return 0;
        }
        JsonToken token = parser.currentToken();
        check(token == JsonToken.START_ARRAY || token == JsonToken.START_OBJECT,
            OfficialSnapshotErrorCode.RECORD_COLLECTION_INVALID);
        boolean array = token == JsonToken.START_ARRAY;
        long size = 0;
        while (parser.nextToken() != (array ? JsonToken.END_ARRAY : JsonToken.END_OBJECT)) {
            check(parser.currentToken() != null, OfficialSnapshotErrorCode.RECORD_COLLECTION_TRUNCATED);
            String child = "*";
            if (!array) {
                child = targets.stream()
                    .anyMatch(target -> target.equals(pointer + "/*") || target.startsWith(pointer + "/*/")) ? "*"
                        : parser.currentName();
                parser.nextToken();
            }
            size += counted ? 1 : count(parser, pointer + "/" + child, targets);
            parser.skipChildren();
        }
        return size;
    }

    /**
     * 按发布协议统计奖励记录数量。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @return 当前操作计算或读取的数值。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private long countRewards(JsonParser parser) throws IOException {
        JsonToken token = parser.currentToken();
        if (token != JsonToken.START_OBJECT && token != JsonToken.START_ARRAY)
            return 0;
        boolean array = token == JsonToken.START_ARRAY;
        long count = 0;
        while (parser.nextToken() != (array ? JsonToken.END_ARRAY : JsonToken.END_OBJECT)) {
            check(parser.currentToken() != null, OfficialSnapshotErrorCode.REWARD_COLLECTION_TRUNCATED);
            String field = array ? "" : parser.currentName();
            if (!array)
                parser.nextToken();
            count += "rewards".equals(field) ? count(parser, "/rewards", Set.of("/rewards")) : countRewards(parser);
        }
        return count;
    }

    /**
     * 校验文件相对路径属于允许的业务文件，拒绝越界路径。
     *
     * @param path 当前文件或业务资源的路径。
     */
    public void validatePath(String path) {
        check(
            path != null && OfficialSnapshotProtocol.BUSINESS_PATH_PATTERN.matcher(path).matches()
                && DIRECTORIES.contains(path.substring(0, path.indexOf('/'))),
            OfficialSnapshotErrorCode.FILE_PATH_INVALID);
    }

    /**
     * 创建禁止重复字段和非法尾随内容的流式解析器。
     *
     * @param path 压缩发布清单文件。
     * @return 协议数据解析器对象。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private JsonParser strictParser(Path path) throws IOException {
        check(path != null && Files.isRegularFile(path) && Files.size(path) > 0
            && Files.size(path) <= properties.getMaxFileBytes(), OfficialSnapshotErrorCode.MANIFEST_SIZE_INVALID);
        InputStream compressed = Files.newInputStream(path);
        try {
            JsonParser parser = objectMapper.getFactory().createParser(new GZIPInputStream(compressed));
            parser.enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION.mappedFeature());
            return parser;
        } catch (IOException | RuntimeException exception) {
            try {
                compressed.close();
            } catch (IOException cleanupFailure) {
                exception.addSuppressed(cleanupFailure);
            }
            throw exception;
        }
    }

    /**
     * 计算内容的 SHA-256 摘要。
     *
     * @return 消息摘要对象。
     */
    private static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

    /**
     * 校验当前输入边界，不满足条件时直接失败。
     *
     * @param expression 表达式。
     * @param errorCode 对应结构错误的稳定错误码。
     */
    private static void check(boolean expression, ErrorCode errorCode) {
        BusinessAssert.isTrue(expression, errorCode);
    }

}
