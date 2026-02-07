package com.example.todayserver.domain.analysis.controller;

import com.example.todayserver.domain.analysis.dto.request.DifficultyRequest;
import com.example.todayserver.domain.analysis.dto.request.FocusChecklistRequest;
import com.example.todayserver.domain.analysis.dto.response.BadgeStatsResponse;
import com.example.todayserver.domain.analysis.dto.response.DifficultyResponse;
import com.example.todayserver.domain.analysis.dto.response.FocusChecklistResponse;
import com.example.todayserver.domain.analysis.dto.response.GrassMapResponse;
import com.example.todayserver.domain.analysis.dto.response.TogetherDaysResponse;
import com.example.todayserver.domain.analysis.dto.response.WeeklyCompletionResponse;
import com.example.todayserver.domain.analysis.service.AnalysisService;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "분석", description = "일정 분석 및 통계 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    @Operation(summary = "요일별 계획 대비 완료율 조회", description = "최근 3개월간 요일별 일정 완료율을 조회합니다.")
    @GetMapping("/weekly-completion-rate")
    public ApiResponse<WeeklyCompletionResponse> getWeeklyCompletionRate(
            @AuthenticationPrincipal Member member) {
        
        WeeklyCompletionResponse response = analysisService.getWeeklyCompletionRate(member);
        return ApiResponse.success("요일별 완료율 조회 성공", response);
    }

    @Operation(summary = "TODAY와 함께 하고 있어요", description = "가입일로부터 경과한 일수를 조회합니다.")
    @GetMapping("/together-days")
    public ApiResponse<TogetherDaysResponse> getTogetherDays(
            @AuthenticationPrincipal Member member) {
        
        TogetherDaysResponse response = analysisService.getTogetherDays(member);
        return ApiResponse.success("경과 일수 조회 성공", response);
    }

    @Operation(summary = "일정소화난이도 평가 등록", description = "특정 날짜의 난이도를 평가합니다.")
    @PostMapping("/difficulty")
    public ApiResponse<DifficultyResponse.Create> createDailyDifficulty(
            @AuthenticationPrincipal Member member,
            @Valid @RequestBody DifficultyRequest.Create request) {
        
        DifficultyResponse.Create response = analysisService.createDailyDifficulty(member, request);
        return ApiResponse.success("난이도 평가 등록 성공", response);
    }

    @Operation(summary = "일정소화난이도 평가 수정", description = "특정 날짜의 난이도 평가를 수정합니다.")
    @PatchMapping("/difficulty")
    public ApiResponse<DifficultyResponse.Update> updateDailyDifficulty(
            @AuthenticationPrincipal Member member,
            @Valid @RequestBody DifficultyRequest.Update request) {
        
        DifficultyResponse.Update response = analysisService.updateDailyDifficulty(member, request);
        return ApiResponse.success("난이도 평가 수정 성공", response);
    }

    @Operation(summary = "잔디맵 조회 (최근 91일)", description = "최근 91일간 일정 완료 현황을 잔디맵 형태로 조회합니다.")
    @GetMapping("/grass-map")
    public ApiResponse<GrassMapResponse> getGrassMap(
            @AuthenticationPrincipal Member member) {
        
        GrassMapResponse response = analysisService.getGrassMap(member);
        return ApiResponse.success("잔디맵 조회 성공", response);
    }

    @Operation(summary = "몰입준비 체크리스트 조회", description = "몰입을 위한 준비 체크리스트를 조회합니다.")
    @GetMapping("/focus-checklist")
    public ApiResponse<FocusChecklistResponse> getFocusChecklist(
            @AuthenticationPrincipal Member member) {
        
        FocusChecklistResponse response = analysisService.getFocusChecklist(member);
        return ApiResponse.success("체크리스트 조회 성공", response);
    }

    @Operation(summary = "몰입준비 체크리스트 항목 수정", description = "체크리스트 항목의 완료 상태를 수정합니다.")
    @PatchMapping("/focus-checklist/{itemId}")
    public ApiResponse<Void> updateFocusChecklistItem(
            @PathVariable Long itemId,
            @Valid @RequestBody FocusChecklistRequest request) {
        
        analysisService.updateFocusChecklistItem(itemId, request);
        return ApiResponse.success("체크리스트 항목 수정 성공", null);
    }

    @Operation(summary = "뱃지 및 완료 일정 통계 조회", description = "얻은 뱃지 수, 완료한 일정 수, 상위 랭킹을 조회합니다.")
    @GetMapping("/badge-stats")
    public ApiResponse<BadgeStatsResponse> getBadgeStats(
            @AuthenticationPrincipal Member member) {
        
        BadgeStatsResponse response = analysisService.getBadgeStats(member);
        return ApiResponse.success("뱃지 통계 조회 성공", response);
    }
}
