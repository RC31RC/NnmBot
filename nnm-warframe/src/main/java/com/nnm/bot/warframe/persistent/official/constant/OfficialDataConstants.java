package com.nnm.bot.warframe.persistent.official.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class OfficialDataConstants {
    public static final int IMPORT_HEAD_ID = 1;
    public static final int RECORD_BATCH_SIZE = 250;
    public static final String METADATA_BKEY = "$metadata";
    public static final String OFFICIAL_SOURCE_CODE = "NNM_DATASET";
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private OfficialDataConstants() {
    }

    public enum MaintenanceType {
        OFFICIAL,
        SELF_MAINTAINED
    }

    public enum ImportStatus {
        PENDING, APPLIED, EXPIRED, ARCHIVED
    }

    public enum ChangeType {
        ADDED, CHANGED, REMOVED, UNCHANGED
    }

    /** 持久化形状码同时区分字段缺失、显式 null 和具体 JSON 类型。 */
    public enum FieldState {
        MISSING(0, null), NULL(1, null), STRING(2, "string"), NUMBER(3, "number"),
        BOOLEAN(4, "boolean"), OBJECT(5, "object"), ARRAY(6, "array");

        private final short code;
        private final String jsonType;

        FieldState(int code, String jsonType) {
            this.code = (short) code;
            this.jsonType = jsonType;
        }

        public short code() {
            return code;
        }

        public String jsonType() {
            return jsonType;
        }

        public static FieldState fromCode(Short code) {
            if (code == null) return MISSING;
            for (FieldState state : values()) {
                if (state.code == code) return state;
            }
            throw new IllegalStateException("未知官方持久化字段形状：" + code);
        }
    }

    public enum SourceMode {
        LOCAL,
        REMOTE
    }
}
