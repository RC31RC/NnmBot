package com.nnm.bot.warframe.persistent.curated.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 人工社区资料的容器、字段和业务键输入约束。 */
@Getter
@RequiredArgsConstructor
public enum CommunityInputErrorCode implements ErrorCode {
    INPUT_TRAILING_JSON(400930, "输入包含多余的尾随 JSON", HttpStatus.BAD_REQUEST),
    INPUT_CONTAINER_INVALID(400931, "输入必须为业务对象、数组或稳定键字典", HttpStatus.BAD_REQUEST),
    RECORD_WRAPPER_INVALID(400932, "records 包装对象不能混入其他字段", HttpStatus.BAD_REQUEST),
    DICTIONARY_KEY_REQUIRED(400933, "字典成员必须具有稳定键", HttpStatus.BAD_REQUEST),
    RECORD_OBJECT_REQUIRED(400934, "社区记录必须为对象", HttpStatus.BAD_REQUEST),
    EXPLICIT_KEY_INVALID(400935, "key 必须是字符串", HttpStatus.BAD_REQUEST),
    ESSENCE_AMOUNT_INVALID(400936, "essenceAmount 必须为非负整数", HttpStatus.BAD_REQUEST),
    RIVEN_ATTRIBUTE_COMBINATION_INVALID(400937, "紫卡别名必须关联一至三个官方属性代码", HttpStatus.BAD_REQUEST),
    RIVEN_ATTRIBUTE_DUPLICATE(400938, "组合不能重复选择同一个官方属性", HttpStatus.BAD_REQUEST),
    RECORD_KEY_INVALID(400939, "业务键必须非空且不超过 500 字符", HttpStatus.BAD_REQUEST),
    RECORD_KEY_DUPLICATE(400940, "输入包含重复业务键", HttpStatus.BAD_REQUEST),
    FIELD_INVALID(400941, "字段无效：%s", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
