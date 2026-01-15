package com.example.todayserver.global.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode implements BaseErrorCode {

    // 공통
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON001", "잘못된 요청입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COMMON002", "입력값이 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON003", "서버 내부 오류가 발생했습니다."),

    // SAMPLE
    SAMPLE_NOT_FOUND(HttpStatus.NOT_FOUND, "SAMPLE001", "샘플 데이터를 찾을 수 없습니다."),

    // Schedule
    SCHEDULE_EVENT_TIME_REQUIRED(HttpStatus.BAD_REQUEST, "SCHEDULE400_1", "일정(EVENT) 등록 시 시작 시각(startAt)과 종료 시각(endAt)은 필수입니다."),
    SCHEDULE_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "SCHEDULE400_2", "요청하신 일정 정보가 올바르지 않습니다."),
    SCHEDULE_EVENT_TIME_ORDER_INVALID(HttpStatus.BAD_REQUEST, "SCHEDULE400_3", "종료 시각(endAt)은 시작 시각(startAt) 이후여야 합니다."),
    SCHEDULE_EVENT_DATETIME_FORMAT_INVALID(HttpStatus.BAD_REQUEST, "SCHEDULE400_4", "startAt/endAt는 'yyyy-MM-dd HH:mm' 형식이어야 합니다."),
    SCHEDULE_REPEAT_TYPE_REQUIRED(HttpStatus.BAD_REQUEST, "SCHEDULE400_5", "repeatType은 필수 입력입니다."),
    SCHEDULE_DURATION_INVALID(HttpStatus.BAD_REQUEST, "SCHEDULE400_6", "duration은 1분 이상이어야 합니다."),
    SCHEDULE_TODO_CUSTOM_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "SCHEDULE400_7", "date는 필수 입력입니다.");
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
