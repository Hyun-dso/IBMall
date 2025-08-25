package com.itbank.mall.dto.admin.grade;

import lombok.Data;

@Data
public class GradeBulkUpdateRequest {
    private String reason;     // ex) "scheduled-batch", "manual by admin"
    private Boolean dryRun;    // true면 변경/로그 미적용, 시뮬레이션만
}
