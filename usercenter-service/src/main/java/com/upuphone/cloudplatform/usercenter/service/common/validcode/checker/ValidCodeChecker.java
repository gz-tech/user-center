package com.upuphone.cloudplatform.usercenter.service.common.validcode.checker;

import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;

public interface ValidCodeChecker {

    /**
     * 校验验证码
     *
     * @param channelCode   手机号码/邮箱/etc
     * @param validCode     验证码
     * @param validCodeType 验证类型
     */
    void check(String channelCode, String deviceId, String validCode, ValidCodeType validCodeType);
}
