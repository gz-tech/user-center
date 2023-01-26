package com.upuphone.cloudplatform.usercenter.service.userbasic.login.handler;

import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUsePasswordRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;

/**
 * @Classname LoginUserPasswordHandler
 * @Description
 * @Date 2022/4/1 4:43 下午
 * @Created by gz-d
 */
public interface LoginPwdHandler {
    UserBaseInfoPo getUser(LoginUsePasswordRequest request);

    LoginResponse handleLogin(UserBaseInfoPo user, String secondLoginSecret);
}
