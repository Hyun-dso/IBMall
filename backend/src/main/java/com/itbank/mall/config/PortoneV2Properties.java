// src/main/java/com/itbank/mall/config/PortoneV2Properties.java
package com.itbank.mall.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "portone.v2")
public class PortoneV2Properties {
    private String secret;     // server secret (V2)
    private String storeId;    // 선택: 일부 API에 사용
    private String channelKey; // 프론트에서 쓰는 값(검증엔 불필요)
    private String baseUrl = "https://api.portone.io"; // 기본값
}
