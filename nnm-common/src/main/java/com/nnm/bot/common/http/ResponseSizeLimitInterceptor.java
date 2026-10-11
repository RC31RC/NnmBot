package com.nnm.bot.common.http;

import com.nnm.bot.common.config.http.HttpClientProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/** 在转换器读取响应前限制字节数，同时保护普通调用、声明式代理和错误响应。 */
public final class ResponseSizeLimitInterceptor implements ClientHttpRequestInterceptor {

    // 仅为进程内请求属性，不作为 Header 发送给上游。
    static final String STREAMING_DOWNLOAD = ResponseSizeLimitInterceptor.class.getName() + ".download";
    private final HttpClientProperties properties;

    public ResponseSizeLimitInterceptor(HttpClientProperties properties) {
        this.properties = properties;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        try {
            long limit = Boolean.TRUE.equals(request.getAttributes().get(STREAMING_DOWNLOAD))
                    && response.getStatusCode().is2xxSuccessful()
                    ? properties.getMaxDownloadBytes() : properties.getMaxResponseBodyBytes();
            // HEAD 的 Content-Length 描述 GET 实体，而非本次响应体，不能据此拒绝。
            if (!"HEAD".equals(request.getMethod().name())
                    && response.getHeaders().getContentLength() > limit) {
                throw tooLarge(request, limit);
            }
            return new LimitedResponse(response, request, limit);
        } catch (IOException | RuntimeException exception) {
            response.close();
            throw exception;
        }
    }

    private static ExternalResponseTooLargeException tooLarge(HttpRequest request, long limit) {
        return new ExternalResponseTooLargeException(
                request.getMethod().name(), HttpClient.sanitizeUrl(request.getURI().toString()),
                limit, null);
    }

    private static final class LimitedResponse implements ClientHttpResponse {
        private final ClientHttpResponse delegate;
        private final HttpRequest request;
        private final long limit;
        private InputStream input;

        private LimitedResponse(ClientHttpResponse delegate, HttpRequest request, long limit) {
            this.delegate = delegate;
            this.request = request;
            this.limit = limit;
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return delegate.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return delegate.getStatusText();
        }

        @Override
        public HttpHeaders getHeaders() {
            return delegate.getHeaders();
        }

        @Override
        public InputStream getBody() throws IOException {
            if (input == null) {
                input = new FilterInputStream(delegate.getBody()) {
                    private long count;

                    @Override
                    public int read() throws IOException {
                        int value = in.read();
                        if (value >= 0) {
                            checkSize(1);
                        }
                        return value;
                    }

                    @Override
                    public int read(byte[] bytes, int offset, int length) throws IOException {
                        Objects.checkFromIndexSize(offset, length, bytes.length);
                        if (length == 0) {
                            return 0;
                        }
                        // 最多多读一个字节来判定超限，不依赖 Content-Length 的真实性。
                        int allowed = (int) Math.min(length, Math.max(1, limit - count + 1));
                        int read = in.read(bytes, offset, allowed);
                        if (read > 0) {
                            checkSize(read);
                        }
                        return read;
                    }

                    @Override
                    public long skip(long amount) throws IOException {
                        if (amount <= 0) {
                            return 0;
                        }
                        byte[] buffer = new byte[(int) Math.min(8192, amount)];
                        long skipped = 0;
                        while (skipped < amount) {
                            int read = read(buffer, 0, (int) Math.min(buffer.length, amount - skipped));
                            if (read < 0) {
                                break;
                            }
                            skipped += read;
                        }
                        return skipped;
                    }

                    @Override
                    public boolean markSupported() {
                        return false;
                    }

                    @Override
                    public void mark(int readLimit) {
                        // 不允许回退读取位置绕过计数。
                    }

                    @Override
                    public void reset() throws IOException {
                        throw new IOException("Response stream does not support reset");
                    }

                    private void checkSize(int read) {
                        count += read;
                        if (count > limit) {
                            throw tooLarge(request, limit);
                        }
                    }
                };
            }
            return input;
        }

        @Override
        public void close() {
            delegate.close();
        }
    }
}
