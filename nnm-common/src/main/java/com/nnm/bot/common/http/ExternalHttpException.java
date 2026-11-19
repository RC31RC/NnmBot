package com.nnm.bot.common.http;

import com.nnm.bot.common.error.SystemException;
import com.nnm.bot.common.constant.ExternalHttpErrorCode;
import org.springframework.http.HttpHeaders;
import java.util.List;

/** 外部 HTTP 返回非成功状态时携带的有界响应诊断。 */
public class ExternalHttpException extends SystemException {

    private final int statusCode;
    private final String responseBody;
    private final HttpHeaders responseHeaders;

    /** 保存上游状态、响应头及受客户端限制的响应体。
     *
     * @param method HTTP 方法
     * @param url 请求地址
     * @param statusCode 上游状态码
     * @param responseBody 有界响应体文本
     * @param responseHeaders 上游响应头
     * @param cause 底层协议或传输原因
     */
    public ExternalHttpException(String method, String url, int statusCode, String responseBody,
        HttpHeaders responseHeaders, Throwable cause) {
        super(ExternalHttpErrorCode.REQUEST_FAILED,
            "External HTTP request failed: " + method + " " + url + " returned " + statusCode, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        HttpHeaders copy = new HttpHeaders();
        if (responseHeaders != null) {
            responseHeaders.forEach((name, values) -> copy.put(name, List.copyOf(values)));
        }
        this.responseHeaders = HttpHeaders.readOnlyHttpHeaders(copy);
    }

    /** 创建不含响应头诊断的异常。
     *
     * @param method HTTP 方法
     * @param url 请求地址
     * @param statusCode 上游状态码
     * @param responseBody 有界响应体文本
     * @param cause 底层原因
     */
    public ExternalHttpException(String method, String url, int statusCode, String responseBody, Throwable cause) {
        this(method, url, statusCode, responseBody, HttpHeaders.EMPTY, cause);
    }

    /** 返回上游 HTTP 状态码。 */
    public int getStatusCode() {
        return statusCode;
    }

    /** 返回经客户端限制的上游响应体。 */
    public String getResponseBody() {
        return responseBody;
    }

    /** 返回不可变的上游响应头快照。 */
    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    /** 判断是否适合对幂等请求进行重试。 */
    public boolean isRetryable() {
        return statusCode == 429 || statusCode >= 500 && statusCode < 600;
    }
}
