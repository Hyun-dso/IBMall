package com.itbank.mall.event.delivery;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryNotifyLineDto {
	private String productName;
	private Integer quantity;
	private Long unitPrice;
	private Long lineTotal;
	private String imageUrl; // product.thumbnail_url
}