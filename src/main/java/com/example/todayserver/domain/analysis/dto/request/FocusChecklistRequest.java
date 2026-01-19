package com.example.todayserver.domain.analysis.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class FocusChecklistRequest {
    
    @NotNull(message = "완료 여부는 필수입니다.")
    private Boolean isCompleted;
}
