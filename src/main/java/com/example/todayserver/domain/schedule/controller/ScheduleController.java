package com.example.todayserver.domain.schedule.controller;

import com.example.todayserver.domain.schedule.dto.EventMonthlyCompletionRes;
import com.example.todayserver.domain.schedule.dto.EventMonthlyListRes;
import com.example.todayserver.domain.schedule.dto.EventMonthlySearchReq;
import com.example.todayserver.domain.schedule.dto.ScheduleCreateReq;
import com.example.todayserver.domain.schedule.service.ScheduleService;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Schedule", description = "일정/할일 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "일정/할 일 등록", description = "인증된 사용자의 일정 또는 할 일을 등록합니다. <br> 작업 유형에 따른 요청 값을 입력해주세요.<br> 관련 내용은 노션 API 명세서에서 확인 가능합니다.")
    @PostMapping
    public ApiResponse<Long> createSchedule(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @RequestBody @Valid ScheduleCreateReq req
    ) {

        Long scheduleId = scheduleService.createSchedule(memberId, req);

        return ApiResponse.success("요청이 성공적으로 처리되었습니다.", scheduleId);
    }

    @Operation(summary = "월별 일정 조회", description = "연도/월/출처 필터 기준으로 일정 목록을 조회합니다.")
    @GetMapping("/events")
    public ApiResponse<EventMonthlyListRes> getMonthlyEvents(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @Valid @ModelAttribute EventMonthlySearchReq req
    ) {

        EventMonthlyListRes res = scheduleService.getMonthlyEvents(memberId, req);

        return ApiResponse.success(res);
    }

    @Operation(summary = "월별 일정 완료 현황 조회", description = "월별 일정 완료 현황을 조회합니다.")
    @GetMapping("/events/completion")
    public ApiResponse<EventMonthlyCompletionRes> getMonthlyEventCompletion(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @RequestParam @NotNull @Min(1970) @Max(3000) Integer year,
            @RequestParam @NotNull @Min(1) @Max(12) Integer month
    ) {

        EventMonthlyCompletionRes res =
                scheduleService.getMonthlyEventCompletion(memberId, year, month);

        return ApiResponse.success(res);
    }
}