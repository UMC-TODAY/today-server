package com.example.todayserver.domain.schedule.controller;

import com.example.todayserver.domain.schedule.dto.*;
import com.example.todayserver.domain.schedule.service.ScheduleService;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;



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

    @Operation(summary = "일정 단건 상세 조회", description = "로그인한 사용자의 일정/할일을 상세 조회합니다.")
    @GetMapping("/{scheduleId}")
    public ApiResponse<ScheduleDetailRes> getScheduleDetail(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @PathVariable Long scheduleId
    ) {
         return ApiResponse.success(scheduleService.getScheduleDetail(memberId, scheduleId));
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

    @Operation(summary = "일정/할일 수정", description = "일정/할일을 수정합니다.")
    @PatchMapping("/{scheduleId}")
    public ApiResponse<ScheduleDetailRes> updateSchedule(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleUpdateReq req
    ) {
        ScheduleDetailRes res = scheduleService.updateSchedule(memberId, scheduleId, req);
        return ApiResponse.success("요청이 성공적으로 처리되었습니다.", res);
    }

    // 일정/할 일의 완료 상태(is_done)를 요청값으로 변경, 변경된 결과(id, is_done)를 반환
    @Operation(summary = "상태 변경 및 완료 처리", description = "일정/할 일의 완료 상태를 변경합니다.")
    @PatchMapping("/{id}/status")
    public ApiResponse<ScheduleStatusUpdateResponse> updateScheduleStatus(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @PathVariable Long id,
            @RequestBody ScheduleStatusUpdateRequest req
    ) {

        ScheduleStatusUpdateResponse res =
                scheduleService.updateScheduleStatus(memberId, id, req);

        return ApiResponse.success("상태가 업데이트되었습니다.", res);
    }

    // 내 일정/할 일을 조건(완료여부/카테고리/날짜/키워드)으로 필터링해서 목록으로 반환
    @Operation(summary = "할일/일정 필터링 및 검색", description = "조건에 따라 내 일정/할일 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<ScheduleSearchItemResponse>> getSchedules(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @RequestParam(value = "is_done", required = false) Boolean isDone,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "schedule_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleDate,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        List<ScheduleSearchItemResponse> res =
                scheduleService.getSchedules(memberId, isDone, category, scheduleDate, keyword);

        return ApiResponse.success(res);
    }

    // 요청한 id 목록에 해당하는 일정/할 일을 일괄 삭제하고 삭제된 개수와 메시지를 반환
    @Operation(summary = "할일/일정 일괄 삭제", description = "선택한 일정/할 일을 한 번에 삭제합니다.")
    @DeleteMapping("/bulk")
    public ApiResponse<ScheduleBulkDeleteResponse> deleteSchedulesBulk(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @RequestBody ScheduleBulkDeleteRequest req
    ) {
        ScheduleBulkDeleteResponse res = scheduleService.deleteSchedulesBulk(memberId, req);

        return ApiResponse.success("요청하신 " + res.getDeleted_count() + "개의 항목이 삭제되었습니다.", res);
    }



}