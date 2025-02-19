package com.lvchuan.config.ai;

import org.springframework.ai.autoconfigure.ollama.OllamaConnectionDetails;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * @description:
 * @author: lvchuan
 * @createTime: 2025-02-19 17:00
 */
@Configuration
public class HttpConfig {

    @Bean
    @Qualifier("OllamaRestClientBuilder")
    public RestClient.Builder ollamaRestClientBuilder() {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(
                HttpClient.newHttpClient());
        requestFactory.setReadTimeout(Duration.ofMinutes(3));
        return RestClient.builder().requestFactory(requestFactory);
    }

    @Bean
    public OllamaApi ollamaApi(OllamaConnectionDetails connectionDetails,
                               @Qualifier("OllamaRestClientBuilder") RestClient.Builder restClientBuilder) {
        return new OllamaApi(connectionDetails.getBaseUrl(), restClientBuilder, WebClient.builder());
    }
}
