package com.upuphone.cloudplatform.usercenter.service.common.validcode.checker;

import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public abstract class AbstractValidCodeChecker implements ValidCodeChecker {

    protected abstract boolean checkValidCodeType(ValidCodeType validCodeType);

    protected abstract void doCheck(String channelCode, String deviceId, String validCode, ValidCodeType validCodeType);

    @Override
    public void check(String channelCode, String deviceId, String validCode, ValidCodeType validCodeType) {
        if (null == validCodeType || !checkValidCodeType(validCodeType)) {
            log.error("checkValidCodeType not supported, type=[{}]", validCodeType);
            throw new BusinessException(UserCenterErrorCode.VALIDCODE_CHECK_NOT_SUPPORTED);
        }
        if (StringUtils.isBlank(deviceId)) {
            log.error("deviceId cannot be null");
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "deviceId不能为空");
        }
        doCheck(channelCode, deviceId, validCode, validCodeType);
    }
}
