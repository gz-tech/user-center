package com.upuphone.cloudplatform.usercenter.service.userbasic.login.handler;

import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUseValidCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;


public interface LoginValidCodeHandler {
    UserBaseInfoPo getUser(LoginUseValidCodeRequest request);

    LoginResponse handleLogin(UserBaseInfoPo user, LoginUseValidCodeRequest request);
}
