package com.upuphone.cloudplatform.usercenter.service.userbasic.login.checker;

import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUseValidCodeRequest;

/**
 * @Classname NewDeviceValideCodeChecker
 * @Description
 * @Date 2022/3/31 5:57 下午
 * @Created by gz-d
 */
public interface NewDeviceUseValideCodeChecker {
    void checkValidCode(LoginUseValidCodeRequest request);

    void checkTicket(String redisKey, String ticket);
}
