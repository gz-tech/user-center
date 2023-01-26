package com.upuphone.cloudplatform.usercenter.errorcode;


import com.upuphone.cloudplatform.common.response.ErrorCode;

public enum HellasErrorCode implements ErrorCode {
    HELLAS_TOKEN_INVALID(302001, "hellatoken校验失败"),
    UPU_USER_NOT_EXIST(302002, "星纪用户不存在");

    private final int errorCode;
    private final String errorMessage;

    HellasErrorCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
