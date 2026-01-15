package com.example.todayserver.domain.schedule.validator;

import com.example.todayserver.domain.schedule.dto.ScheduleCreateReq;
import com.example.todayserver.domain.schedule.enums.Mode;
import com.example.todayserver.domain.schedule.enums.ScheduleType;
import com.example.todayserver.global.common.exception.CustomException;
import com.example.todayserver.global.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class ScheduleCreateValidator {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // 일정/할 일 생성 유효성 검사
    public void validateForCreate(ScheduleCreateReq req) {
        if (req.scheduleType() == null || req.mode() == null) {
            // scheduleType 또는 mode 누락
            throw new CustomException(ErrorCode.SCHEDULE_INVALID_REQUEST);
        }

        // 타입에 따른 분기 처리
        if (req.scheduleType() == ScheduleType.TASK) {
            validateTodo(req);
        } else if (req.scheduleType() == ScheduleType.EVENT) {
            validateEvent(req);
        } else {
            // 정의되지 않은 scheduleType
            throw new CustomException(ErrorCode.SCHEDULE_INVALID_REQUEST);
        }
    }

    // 할 일 타입 검증 분기 처리
    private void validateTodo(ScheduleCreateReq req) {
        if (req.mode() == Mode.CUSTOM) {
            validateTodoCustom(req);
        } else if (req.mode() == Mode.ANYTIME) {
            validateTodoAnytime(req);
        } else {
            // 정의되지 않은 mode
            throw new CustomException(ErrorCode.SCHEDULE_INVALID_REQUEST);
        }
    }

    // 할일 - 사용자 지정 요청 값 검증
    private void validateTodoCustom(ScheduleCreateReq req) {
        // date 필수
        if (req.date() == null) {
            throw new CustomException(ErrorCode.SCHEDULE_TODO_CUSTOM_DATE_REQUIRED);
        }
        // duration 필수 + 1분 이상
        if (req.duration() == null || req.duration() <= 0) {
            throw new CustomException(ErrorCode.SCHEDULE_DURATION_INVALID);
        }
        // repeatType 필수
        if (req.repeatType() == null) {
            throw new CustomException(ErrorCode.SCHEDULE_REPEAT_TYPE_REQUIRED);
        }
    }

    // 할일 - 언제든지 요청 값 검증
    private void validateTodoAnytime(ScheduleCreateReq req) {
        // duration 필수 + 1분 이상
        if (req.duration() == null || req.duration() <= 0) {
            throw new CustomException(ErrorCode.SCHEDULE_DURATION_INVALID);
        }
    }

    // 일정 - 사용자 지정 요청 값 검증
    private void validateEvent(ScheduleCreateReq req) {
        if (req.mode() != Mode.CUSTOM) {
            // 현재 EVENT는 CUSTOM 모드만 허용
            throw new CustomException(ErrorCode.SCHEDULE_INVALID_REQUEST);
        }
        validateEventCustom(req);
    }

    // 일정 - 사용자 지정 요청 값 검증
    private void validateEventCustom(ScheduleCreateReq req) {
        // 시작/종료 값 존재 여부 체크
        if (req.startAt() == null || req.endAt() == null ||
                req.startAt().isBlank() || req.endAt().isBlank()) {
            throw new CustomException(ErrorCode.SCHEDULE_EVENT_TIME_REQUIRED);
        }

        // 문자열 포맷 검증 + 시간 순서 검증
        try {
            LocalDateTime start = LocalDateTime.parse(req.startAt(), DATE_TIME_FORMATTER);
            LocalDateTime end = LocalDateTime.parse(req.endAt(), DATE_TIME_FORMATTER);

            // endAt이 startAt보다 과거일 경우 예외 처리
            if (end.isBefore(start)) {
                throw new CustomException(ErrorCode.SCHEDULE_EVENT_TIME_ORDER_INVALID);
            }

        } catch (DateTimeParseException e) {
            // 형식이 "yyyy-MM-dd HH:mm"이 아닐 경우 에외 처리
            throw new CustomException(ErrorCode.SCHEDULE_EVENT_DATETIME_FORMAT_INVALID);
        }

        // 반복 주기 체크
        if (req.repeatType() == null) {
            throw new CustomException(ErrorCode.SCHEDULE_REPEAT_TYPE_REQUIRED);
        }
    }
}
