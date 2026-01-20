package com.example.todayserver.domain.member.excpetion;

import com.example.todayserver.global.common.exception.BaseErrorCode;
import com.example.todayserver.global.common.exception.CustomException;

public class PreferenceException extends CustomException {
    public PreferenceException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
