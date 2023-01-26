package com.upuphone.cloudplatform.usercenter.service.userbasic.register.registstrategy;

import com.upuphone.cloudplatform.usercenter.service.userbasic.register.model.RegistReqBo;

public interface RegistStrategy {

    String getUniqueKey(RegistReqBo registRequest);

    void checkKeyNumberUnique(RegistReqBo registRequest);
}
