package com.upuphone.cloudplatform.usercenter.api;

import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.vo.request.oauth.AuthorizationCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.oauth.AuthorizationCodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "user-center", contextId = "oauth")
public interface OauthClient {

    @PostMapping("/oauth/authorization_code")
    @ResponseBody
    CommonResponse<AuthorizationCodeResponse> authorizationCode(@RequestBody AuthorizationCodeRequest request) throws Exception;
}
