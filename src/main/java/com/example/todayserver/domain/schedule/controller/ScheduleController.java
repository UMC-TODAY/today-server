package com.example.todayserver.domain.schedule.controller;

import com.example.todayserver.domain.schedule.dto.EventMonthlyListRes;
import com.example.todayserver.domain.schedule.dto.EventMonthlySearchReq;
import com.example.todayserver.domain.schedule.dto.ScheduleCreateReq;
import com.example.todayserver.domain.schedule.service.ScheduleService;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Schedule", description = "일정/할일 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "일정/할 일 등록", description = "인증된 사용자의 일정 또는 할 일을 등록합니다.")
    @PostMapping
    public ApiResponse<Long> createSchedule(
            @RequestParam("memberId") Long memberId, // 임시 파라미터 추후 인증정보로 대체 예정
            @RequestBody @Valid ScheduleCreateReq req
    ) {

        Long scheduleId = scheduleService.createSchedule(memberId, req);

        return ApiResponse.success("요청이 성공적으로 처리되었습니다.", scheduleId);
    }

    @Operation(summary = "월별 일정 조회", description = "연도/월/출처 필터 기준으로 일정 목록을 조회합니다.")
    @GetMapping("/events")
    public ApiResponse<EventMonthlyListRes> getMonthlyEvents(
            @RequestParam("memberId") Long memberId,              // 임시 파라미터 추후 인증정보로 대체 예정
            @Valid @ModelAttribute EventMonthlySearchReq req
    ) {

        EventMonthlyListRes res = scheduleService.getMonthlyEvents(memberId, req);

        return ApiResponse.success(res);
    }
}