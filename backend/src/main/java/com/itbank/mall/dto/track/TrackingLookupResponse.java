package com.itbank.mall.dto.track;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrackingLookupResponse {
    private String trackingNumber;
    private String status;
    private String message;
}
