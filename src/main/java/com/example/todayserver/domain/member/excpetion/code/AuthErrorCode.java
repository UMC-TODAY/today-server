package com.example.todayserver.domain.member.excpetion.code;

import com.example.todayserver.global.common.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    CODE_ERROR(HttpStatus.SERVICE_UNAVAILABLE,
            "EMAIL_VERIFY_503",
            "현재 이메일 인증번호를 발급할 수 없습니다."),

    CODE_NOT_EXIST(HttpStatus.BAD_REQUEST,
            "EMAIL_VERIFY_400_1",
            "잘못된 인증번호입니다."),

    CODE_EXPIRED(HttpStatus.BAD_REQUEST,
            "EMAIL_VERIFY_400_2",
            "만료된 인증번호입니다."),

    INVALID_EMAIL(HttpStatus.BAD_REQUEST,
            "EMAIL_VERIFY_400_3",
            "인증되지 않은 이메일입니다."),

    INVALID_SOCIAL_TYPE(HttpStatus.BAD_REQUEST,
            "SOCIAL_TYPE_400",
            "지원하지 않는 소셜 타입입니다."),

    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,
            "TOKEN_404",
            "해당 토큰이 존재하지 않습니다."),

    INVALID_TOKEN(HttpStatus.BAD_REQUEST,
            "TOKEN_400",
            "이미 만료된 토큰입니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;
}
