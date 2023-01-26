package com.upuphone.cloudplatform.usercenter.service.common.validcode.validate;

public interface ValidCodeValidateStrategy<T> {

    void validate(T request);
}
