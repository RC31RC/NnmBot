package com.nnm.bot.common.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class HttpUtils {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private HttpUtils() {}

    public static HttpResult get(String url) {
        return get(url, Map.of());
    }

    public static HttpResult get(String url, Map<String, String> headers) {
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .GET();
        headers.forEach(builder::header);

        try {
            HttpResponse<String> response = CLIENT.send(builder.build(),
                    HttpResponse.BodyHandlers.ofString());
            return new HttpResult(response.statusCode(), response.body(), null);
        } catch (Exception e) {
            return new HttpResult(-1, null, e);
        }
    }

    public static HttpResult post(String url, String body, Map<String, String> headers) {
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body));
        headers.forEach(builder::header);

        try {
            HttpResponse<String> response = CLIENT.send(builder.build(),
                    HttpResponse.BodyHandlers.ofString());
            return new HttpResult(response.statusCode(), response.body(), null);
        } catch (Exception e) {
            return new HttpResult(-1, null, e);
        }
    }

    public record HttpResult(int statusCode, String body, Exception error) {

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }
    }
}
