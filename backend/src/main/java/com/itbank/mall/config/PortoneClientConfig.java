// src/main/java/com/itbank/mall/config/PortoneClientConfig.java
package com.itbank.mall.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class PortoneClientConfig {

    private final PortoneV2Properties props;

    @Bean
    public WebClient portoneWebClient() {
        var httpClient = reactor.netty.http.client.HttpClient.create()
            .responseTimeout(java.time.Duration.ofSeconds(7))
            .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);

        return WebClient.builder()
            .baseUrl(props.getBaseUrl()) // https://api.portone.io
            .defaultHeader("Authorization", "PortOne " + props.getSecret())
            // 필요 시 아래 두 줄은 "결제 생성/사전검증" 호출할 때만 켜세요
            // .defaultHeader("X-Store-Id", props.getStoreId())
            // .defaultHeader("X-Channel-Key", props.getChannelKey())
            .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build())
            .build();
    }
}
