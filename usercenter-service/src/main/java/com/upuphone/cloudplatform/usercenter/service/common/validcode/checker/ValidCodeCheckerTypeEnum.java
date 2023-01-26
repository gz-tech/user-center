package com.upuphone.cloudplatform.usercenter.service.common.validcode.checker;

public enum ValidCodeCheckerTypeEnum {

    SMS("smsValidCodeChecker"),
    EMAIL("emailValidCodeChecker"),
    ;

    private final String checkerType;

    ValidCodeCheckerTypeEnum(String checkerType) {
        this.checkerType = checkerType;
    }

    public String getCheckerType() {
        return checkerType;
    }
}
