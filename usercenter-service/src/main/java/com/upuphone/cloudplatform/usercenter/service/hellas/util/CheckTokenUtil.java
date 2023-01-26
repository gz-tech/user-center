package com.upuphone.cloudplatform.usercenter.service.hellas.util;

import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.api.constant.RemoteSourceEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.HellasErrorCode;
import com.upuphone.cloudplatform.usercenter.remote.token.TokenValidateRemoteService;
import com.upuphone.cloudplatform.usercenter.remote.token.model.LotusTokenValidateRequest;
import com.upuphone.cloudplatform.usercenter.remote.token.model.TokenValidateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CheckTokenUtil {

    @Autowired
    private TokenValidateRemoteService tokenValidateRemoteService;

    public void checkToken(String token, String userId, String mobile, RemoteSourceEnum source) {
        TokenValidateRequest tokenValidateRequest = getRequest(source);
        tokenValidateRequest.setAccessToken(token);
        tokenValidateRequest.setUserId(userId);
        tokenValidateRequest.setMobile(mobile);
        boolean isValidToken = tokenValidateRemoteService.process(tokenValidateRequest);
        if (!isValidToken) {
            throw new BusinessException(HellasErrorCode.HELLAS_TOKEN_INVALID);
        }
    }

    private TokenValidateRequest getRequest(RemoteSourceEnum source) {
        if (source == RemoteSourceEnum.LOTUS) {
            return new LotusTokenValidateRequest();
        }
        log.error("找不到对应的source类型");
        throw new BusinessException(CommonErrorCode.BUSINESS_ERROR);
    }
}
