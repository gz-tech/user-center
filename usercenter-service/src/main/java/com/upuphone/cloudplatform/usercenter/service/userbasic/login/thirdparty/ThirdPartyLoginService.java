package com.upuphone.cloudplatform.usercenter.service.userbasic.login.thirdparty;


import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.model.UserThirdAccountBaseInfo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.model.ThirdPartyAccessTokenBo;

public interface ThirdPartyLoginService {
    ThirdPartyAccessTokenBo getThirdPartyAccessTokenByCode(String code);

    UserThirdAccountBaseInfo getThirdPartyUserInfoByAccessToken(ThirdPartyAccessTokenBo bo);
}
