package com.nnm.bot.core.constant;

import com.nnm.bot.core.plugin.api.CommandSpecification;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 本模块命令的统一定义：稳定编码、中文名称与排序。
 *
 * <p>
 * 注解必须使用编译期字符串常量，因此保留本类编码字段；对应元数据在同文件的 {@link Definition} 中维护。 Provider 仅负责将此目录注册给 Core，不再维护第二份名称映射。
 * </p>
 */
public final class SystemCommandCodes {
    public static final String GAME_CODE = "system";

    public static final String HELP = "help";
    public static final String STATUS = "status";
    public static final String SIGN_IN = "sign-in";
    public static final String BIND = "bind";
    public static final String JM = "jm";

    /** 强类型命令定义，统一维护编码、中文名称与排序。 */
    @Getter
    @Accessors(fluent = true)
    public enum Definition implements CommandSpecification {
        HELP(SystemCommandCodes.HELP, "帮助", Integer.MIN_VALUE),
        STATUS(SystemCommandCodes.STATUS, "状态", Integer.MIN_VALUE + 1),
        SIGN_IN(SystemCommandCodes.SIGN_IN, "签到", Integer.MIN_VALUE + 2),
        BIND(SystemCommandCodes.BIND, "绑定", Integer.MIN_VALUE + 3), JM(SystemCommandCodes.JM, "JM 下载", 30);

        private final String code;
        private final String displayName;
        private final int order;

        /**
         * 定义命令的稳定编码、中文名称和排序。
         *
         * @param code 稳定命令编码
         * @param displayName 默认中文名称
         * @param order 展示及匹配优先级
         */
        Definition(String code, String displayName, int order) {
            this.code = code;
            this.displayName = displayName;
            this.order = order;
        }
    }

    /** 启动时构建一次不可变索引，禁止重复编码，不在每次匹配时遍历枚举。 */
    public static final Map<String, Definition> DEFINITIONS =
        Arrays.stream(Definition.values()).collect(Collectors.toUnmodifiableMap(Definition::code, Function.identity()));

    public static final Set<String> ALL = DEFINITIONS.keySet();

    /** 禁止实例化，仅使用本类的稳定定义。 */
    private SystemCommandCodes() {}
}
