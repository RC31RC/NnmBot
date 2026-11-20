package com.nnm.bot.common.http;

import cn.hutool.core.text.CharSequenceUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** HTTP 日志和返回给管理端的诊断统一去除凭据、查询参数和换行。 */
public final class HttpDiagnostics {
    private static final Pattern URL_PATTERN = Pattern.compile("(?i)(https?://[^\\s?#]+)(?:\\?[^\\s#]*)?(?:#[^\\s]*)?");
    private static final int MAX_MESSAGE_LENGTH = 300;

    private HttpDiagnostics() {}

    /**
     * 移除用户信息、查询参数和片段，保留可识别的请求路径。
     *
     * @param url 原始 URL
     * @return 脱敏 URL；无法解析时返回固定占位值
     */
    public static String safeUrl(String url) {
        if (url == null) {
            return "";
        }
        try {
            URI uri = URI.create(url);
            return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), null, null).toString();
        } catch (IllegalArgumentException | URISyntaxException exception) {
            return "[invalid-url]";
        }
    }

    /**
     * 清理错误消息中的凭据风险、换行及超长内容。
     *
     * @param message 原始诊断消息
     * @param requestUrl 对应请求地址
     * @return 可安全用于日志或管理端诊断的短文本
     */
    public static String safeFailureMessage(String message, String requestUrl) {
        if (CharSequenceUtil.isBlank(message)) {
            return "-";
        }
        String normalized = CharSequenceUtil.trim(message.replace('\r', ' ').replace('\n', ' '));
        if (CharSequenceUtil.isNotBlank(requestUrl)) {
            normalized = normalized.replace(requestUrl, safeUrl(requestUrl));
        }
        Matcher matcher = URL_PATTERN.matcher(normalized);
        StringBuilder result = new StringBuilder(normalized.length());
        while (matcher.find()) {
            matcher.appendReplacement(result, Matcher.quoteReplacement(safeUrl(matcher.group())));
        }
        matcher.appendTail(result);
        String safeMessage = result.toString();
        return safeMessage.length() <= MAX_MESSAGE_LENGTH ? safeMessage
            : safeMessage.substring(0, MAX_MESSAGE_LENGTH) + "...(truncated)";
    }
}
