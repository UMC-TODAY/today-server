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
    SCHEDULE_TODO_CUSTOM_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "SCHEDULE400_7", "date는 필수 입력입니다."),

    // External
    EXTERNAL_OAUTH_STATE_INVALID(HttpStatus.BAD_REQUEST, "EXTERNAL400_1", "외부 연동 state 값이 올바르지 않습니다."),
    EXTERNAL_ACCOUNT_INACTIVE(HttpStatus.BAD_REQUEST, "EXTERNAL400_2", "비활성화된 외부 계정입니다."),
    EXTERNAL_PROVIDER_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "EXTERNAL400_3", "지원하지 않는 외부 연동 제공자입니다."),
    EXTERNAL_ACCESS_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "EXTERNAL400_4", "외부 연동 계정의 액세스 토큰이 존재하지 않습니다."),
    EXTERNAL_CALENDAR_INVALID_URL(HttpStatus.BAD_REQUEST, "EXTERNAL400_5", "ICS URL 형식이 올바르지 않습니다."),
    EXTERNAL_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "EXTERNAL404_1", "연결된 외부 계정을 찾을 수 없습니다."),
    EXTERNAL_SOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "EXTERNAL404_2", "외부 캘린더 소스를 찾을 수 없습니다."),
    EXTERNAL_CLIENT_NOT_REGISTERED(HttpStatus.INTERNAL_SERVER_ERROR, "EXTERNAL500_1", "외부 연동 클라이언트가 등록되지 않았습니다."),
    EXTERNAL_MAPPER_NOT_REGISTERED(HttpStatus.INTERNAL_SERVER_ERROR, "EXTERNAL500_2", "외부 이벤트 매퍼가 등록되지 않았습니다."),
    EXTERNAL_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EXTERNAL500_3", "외부 일정 동기화 중 오류가 발생했습니다."),
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "EXTERNAL500_4", "외부 캘린더 API 호출 중 오류가 발생했습니다."),
    EXTERNAL_CALENDAR_FETCH_FAILED(HttpStatus.BAD_GATEWAY, "EXTERNAL502_1", "외부 캘린더(ICS) 조회에 실패했습니다."),
    EXTERNAL_CALENDAR_PARSE_FAILED(HttpStatus.BAD_GATEWAY, "EXTERNAL502_2", "외부 캘린더(ICS) 파싱에 실패했습니다."),

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
