package com.example.todayserver.domain.analysis.controller;

import com.example.todayserver.domain.analysis.dto.response.WeeklyCompletionRes;
import com.example.todayserver.domain.analysis.service.AnalysisService;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "분석", description = "일정 분석 및 통계 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    @Operation(summary = "요일별 계획 대비 완료율 조회", description = "최근 3개월간 요일별 일정 완료율을 조회합니다.")
    @GetMapping("/weekly-completion-rate")
    public ApiResponse<WeeklyCompletionRes> getWeeklyCompletionRate(
            @AuthenticationPrincipal Member member) {
        
        WeeklyCompletionRes response = analysisService.getWeeklyCompletionRate(member);
        return ApiResponse.success("요일별 완료율 조회 성공", response);
    }
}
