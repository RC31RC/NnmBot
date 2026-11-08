package com.nnm.bot.warframe.persistent.official.constant;

import com.nnm.bot.common.exception.SystemException;

import java.time.ZoneId;
import java.util.Map;
import java.util.Set;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class OfficialDataConstants {
    public static final int IMPORT_HEAD_ID = 1;
    public static final int RECORD_BATCH_SIZE = 250;
    public static final int RECORD_NAME_MAX_LENGTH = 500;
    public static final String APPLY_TASK_PREFIX = "WARFRAME_OFFICIAL_APPLY:";
    public static final String REFRESH_TASK_PREFIX = "WARFRAME_OFFICIAL_REFRESH:";
    public static final String REFRESH_ALL_TASK_PREFIX = "WARFRAME_OFFICIAL_REFRESH_ALL:";
    public static final String ACTIVE_REFRESH_PREFIX = "warframe-official-refresh-active:";
    public static final String ALL_SCOPE = "ALL";
    public static final String REFRESH_PERMISSION = "warframe:game:refresh";
    public static final String PREVIEW_CLEANUP_CRON = "0 */5 * * * *";
    public static final ZoneId PREVIEW_CLEANUP_ZONE = ZoneId.of("Asia/Shanghai");
    public static final String METADATA_BKEY = "$metadata";
    public static final String OFFICIAL_SOURCE_CODE = "NNM_DATASET";
    /** 与解析器业务键前缀一致，新增自维护资料也按相同分组参与分页。 */
    public static final Map<WarframeOfficialDataDomain,
        Set<String>> RECORD_GROUPS = Map.of(WarframeOfficialDataDomain.DOJO_RECIPE,
            Set.of("research", "fabrications", "rooms", "decos", "colours", "backdrops"),
            WarframeOfficialDataDomain.NIGHTWAVE, Set.of("challenges", "rewards"), WarframeOfficialDataDomain.BOUNTY,
            Set.of("definitions", "rewardPools"), WarframeOfficialDataDomain.DROP_TABLE,
            Set.of("contexts", "regionResources"), WarframeOfficialDataDomain.ENEMY,
            Set.of("agents", "avatars", "damageControllers", "droptables", "hitProxies", "aiWeapons"),
            WarframeOfficialDataDomain.CODEX, Set.of("objects", "loreFragments", "songs", "fighterFrames"),
            WarframeOfficialDataDomain.ACTIVITY, Set.of("conquests", "descents"));

    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private OfficialDataConstants() {}

    /** 官方数据刷新及审核所使用的稳定互斥用途。 */
    public enum LockPurpose {
        ADMISSION("admission"), DATASET("dataset"), METADATA("metadata"), PREPARATION("approval-prepare");

        private final String key;

        LockPurpose(String key) {
            this.key = key;
        }

        public String key() {
            return key;
        }
    }

    public enum MaintenanceType {
        OFFICIAL, SELF_MAINTAINED
    }

    public enum ImportStatus {
        PENDING, APPLYING, PARTIAL_FAILED, APPLIED, EXPIRED, ARCHIVED
    }

    /** JPA 规定的 SKIP LOCKED 哨兵值，-2 表示不要等待别的实例持有的行锁。 */
    public static final String APPLY_SKIP_LOCKED_HINT = "-2";

    public enum ApplyFileStatus {
        READY, RUNNING, DONE, FAILED
    }

    /** 官方数据任务进度阶段，与持久化审批状态的用途分离。 */
    public enum ImportStage {
        RESOLVING_VERSION, DOWNLOADING_DATASET, VERIFYING_DATASET, PREVIEW, APPLYING
    }

    public enum ChangeType {
        ADDED, CHANGED, REMOVED, UNCHANGED
    }

    /** 持久化形状码同时区分字段缺失、显式 null 和具体 JSON 类型。 */
    public enum FieldState {
        MISSING(0, null), NULL(1, null), STRING(2, "string"), NUMBER(3, "number"), BOOLEAN(4, "boolean"),
        OBJECT(5, "object"), ARRAY(6, "array");

        private final short code;
        private final String jsonType;

        FieldState(int code, String jsonType) {
            this.code = (short)code;
            this.jsonType = jsonType;
        }

        public short code() {
            return code;
        }

        public String jsonType() {
            return jsonType;
        }

        public static FieldState fromCode(Short code) {
            if (code == null)
                return MISSING;
            for (FieldState state : values()) {
                if (state.code == code)
                    return state;
            }
            throw new SystemException(OfficialDataErrorCode.FIELD_STATE_INVALID, "未知官方持久化字段形状：" + code);
        }
    }

    public enum SourceMode {
        LOCAL, REMOTE
    }
}
