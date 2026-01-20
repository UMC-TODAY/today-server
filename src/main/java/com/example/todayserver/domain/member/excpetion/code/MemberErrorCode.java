package com.example.todayserver.domain.member.excpetion.code;

import com.example.todayserver.global.common.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND,
            "MEMBER404_1",
                    "해당 사용자를 찾지 못했습니다."),
    INVALID_PW(HttpStatus.METHOD_NOT_ALLOWED,
            "VALID400_1",
                    "비밀번호가 올바르지 않습니다."),
    EXIST_EMAIL(HttpStatus.BAD_REQUEST,
            "EMAIL_VERIFY_400",
            "이미 가입된 이메일입니다."),
    DUPLICATE_SOCIAL(HttpStatus.BAD_REQUEST,
            "MEMBER400_1",
            "다른 방식으로 가입된 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST,
            "MEMBER400_2",
            "이미 사용중인 닉네임입니다."),
    WITHDRAW_EMAIL(HttpStatus.BAD_REQUEST,
            "EMAIL_VERIFY_400_4",
            "탈퇴한지 7일 이내인 계정으로 가입하실 수 없습니다."),
    NO_PASSWORD(HttpStatus.BAD_REQUEST,
            "PASSWORD_400",
            "소셜 가입자는 비밀번호를 재설정 하실 수 없습니다."),
    FILE_TYPE_ERROR(HttpStatus.BAD_REQUEST,
            "FILE400",
            "이미지 파일만 업로드 가능합니다."),
    IMAGE_UPLOAD_FAIL(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "FILE404",
            "프로필 이미지 업로드에 실패하였습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
