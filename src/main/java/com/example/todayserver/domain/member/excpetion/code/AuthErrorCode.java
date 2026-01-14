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
            "만료된 인증번호입니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;
}
