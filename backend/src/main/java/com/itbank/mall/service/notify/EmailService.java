// src/main/java/com/itbank/mall/service/notify/EmailService.java
package com.itbank.mall.service.notify;

public interface EmailService {
    void send(String to, String subject, String body);
}
