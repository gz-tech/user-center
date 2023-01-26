package com.upuphone.cloudplatform.usercenter.service.authentication;

import com.google.common.base.Strings;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.auth.vo.checktoken.CheckAccessTokenRequest;
import com.upuphone.cloudplatform.usercenter.entity.token.TokenBo;
import com.upuphone.cloudplatform.usercenter.service.authentication.model.CheckAccessTokenResult;
import com.upuphone.cloudplatform.usercenter.service.util.AccessTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckAccessTokenService extends BaseService<CheckAccessTokenRequest, CheckAccessTokenResult> {


    @Autowired
    private AccessTokenUtil accessTokenUtil;

    @Override
    protected void validate(CheckAccessTokenRequest request) {
        if (Strings.isNullOrEmpty(request.getAccessToken())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "access token can not be null");
        }
    }

    @Override
    protected CheckAccessTokenResult processCore(CheckAccessTokenRequest soaRequest) throws Exception {

        TokenBo tokenBO = accessTokenUtil.parseToken(soaRequest.getAccessToken());

        CheckAccessTokenResult result = new CheckAccessTokenResult();
        result.setAccessToken(soaRequest.getAccessToken());
        result.setExpirationTime(tokenBO.getExpireTime());
        result.setStartValidTime(tokenBO.getStartValidTime());
        result.setUserId(tokenBO.getUserId());
        result.setExpired(tokenBO.getExpired());
        return result;
    }
}
