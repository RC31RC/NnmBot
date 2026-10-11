package com.nnm.bot.common.http;

import cn.hutool.core.util.StrUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** HTTP 日志和返回给管理端的诊断统一去除凭据、查询参数和换行。 */
public final class HttpDiagnostics {
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(?i)(https?://[^\\s?#]+)(?:\\?[^\\s#]*)?(?:#[^\\s]*)?");
    private static final int MAX_MESSAGE_LENGTH = 300;

    private HttpDiagnostics() {
    }

    public static String safeUrl(String url) {
        if (url == null) {
            return "";
        }
        try {
            URI uri = URI.create(url);
            return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(),
                    uri.getPath(), null, null).toString();
        } catch (IllegalArgumentException | URISyntaxException exception) {
            return "[invalid-url]";
        }
    }

    public static String safeFailureMessage(String message, String requestUrl) {
        if (StrUtil.isBlank(message)) {
            return "-";
        }
        String normalized = message.replace('\r', ' ').replace('\n', ' ').trim();
        if (StrUtil.isNotBlank(requestUrl)) {
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
