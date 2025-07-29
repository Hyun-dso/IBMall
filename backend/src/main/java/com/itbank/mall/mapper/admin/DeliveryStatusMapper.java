
package com.itbank.mall.mapper.admin;

import org.apache.ibatis.annotations.Param;

public interface DeliveryStatusMapper {
	int updateOrderStatus(
			@Param("orderId") Long orderId, 
			@Param("status") String status,
			@Param("trackingNumber") String trackingNumber);
}
