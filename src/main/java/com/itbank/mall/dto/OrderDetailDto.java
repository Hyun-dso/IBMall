package com.itbank.mall.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
//OrderDetailDto.java
public class OrderDetailDto {
	private Long orderId;
	private LocalDateTime orderDate;
	private String orderStatus;
	private int totalPrice;
	private long memberId;
	private String trackingNumber;
	private String deliveryStatus;
	private String recipient;
	private String address;
	private String phone;

	private List<OrderItemDto> items;
}
