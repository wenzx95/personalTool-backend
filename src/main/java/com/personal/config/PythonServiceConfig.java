package com.personal.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Python服务客户端配置
 * 用于调用Python股票服务
 *
 * 配置优先级：数据库配置 > application.yml配置 > 默认值
 * 数据库配置键：python.service.url, python.service.timeout 等
 */
@Configuration
@Slf4j
public class PythonServiceConfig {

    @Value("${python.service.url:http://localhost:8000}")
    private String baseUrl;

    @Value("${python.service.timeout:60000}")
    private int timeout;

    @Value("${python.service.connection.timeout:5000}")
    private int connectionTimeout;

    @Value("${python.service.max.in.memory.size:10485760}")
    private int maxInMemorySize;

    @Bean
    public WebClient pythonWebClient() {
        log.info("初始化Python WebClient: base_url={}, timeout={}ms, connection_timeout={}ms",
                baseUrl, timeout, connectionTimeout);

        // 配置HttpClient，设置超时时间
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .responseTimeout(Duration.ofMillis(timeout))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySize))
                .build();
    }
}
