package com.nnm.bot.warframe.persistent.official.constant;

import java.util.regex.Pattern;

/** 官方数据发布包协议的固定字段与输入形状。 */
public final class OfficialSnapshotProtocol {
    public static final int SCHEMA_VERSION = 3;
    public static final String PRODUCER = "warframe-datasource-nnm";
    public static final String GZIP_MEDIA_TYPE = "application/gzip";
    public static final Pattern BUILD_ID_PATTERN = Pattern.compile("[A-Za-z0-9._-]{1,160}");
    public static final Pattern BUSINESS_PATH_PATTERN =
        Pattern.compile("[a-z][a-z0-9-]*/[a-z0-9][a-z0-9-]*\\.json\\.gz");

    /** 禁止实例化发布包协议常量类。 */
    private OfficialSnapshotProtocol() {}
}
