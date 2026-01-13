package com.example.todayserver.domain.member.excpetion;

import com.example.todayserver.global.common.exception.BaseErrorCode;
import com.example.todayserver.global.common.exception.CustomException;

public class AuthException extends CustomException {
    public AuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
