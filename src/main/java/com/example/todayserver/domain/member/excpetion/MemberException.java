package com.example.todayserver.domain.member.excpetion;

import com.example.todayserver.global.common.exception.BaseErrorCode;
import com.example.todayserver.global.common.exception.CustomException;

public class MemberException extends CustomException {
    public MemberException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
