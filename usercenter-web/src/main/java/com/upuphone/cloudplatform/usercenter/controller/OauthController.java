package com.upuphone.cloudplatform.usercenter.controller;

import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.api.OauthClient;
import com.upuphone.cloudplatform.usercenter.service.oauth.GetAuthoricationCodeService;
import com.upuphone.cloudplatform.usercenter.vo.request.oauth.AuthorizationCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.oauth.AuthorizationCodeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "认证相关 API")
@RestController
public class OauthController implements OauthClient {

    @Autowired
    private GetAuthoricationCodeService getAuthoricationCodeService;

    @Override
    @ApiOperation(value = "获取authorizationCode")
    public CommonResponse<AuthorizationCodeResponse> authorizationCode(AuthorizationCodeRequest request) throws Exception {
        AuthorizationCodeResponse result = getAuthoricationCodeService.process(request);
        CommonResponse<AuthorizationCodeResponse> response = new CommonResponse<>(result);
        return response;
    }
}
