package com.upuphone.cloudplatform.usercenter.controller;

import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.auth.api.AuthenticationClient;
import com.upuphone.cloudplatform.usercenter.auth.vo.authcode.GenerateAuthCodeRequest;
import com.upuphone.cloudplatform.usercenter.auth.vo.authcode.GenerateAuthCodeResponse;
import com.upuphone.cloudplatform.usercenter.auth.vo.checkapprovestate.CheckApproveStateResponse;
import com.upuphone.cloudplatform.usercenter.auth.vo.checktoken.CheckAccessTokenRequest;
import com.upuphone.cloudplatform.usercenter.auth.vo.checktoken.CheckAccessTokenResponse;
import com.upuphone.cloudplatform.usercenter.auth.vo.generatetoken.GenerateTokenRequest;
import com.upuphone.cloudplatform.usercenter.auth.vo.generatetoken.GenerateTokenResponse;
import com.upuphone.cloudplatform.usercenter.auth.vo.getuserbaseinfo.GetUserBaseInfoResponse;
import com.upuphone.cloudplatform.usercenter.auth.vo.refresh.RefreshTokenRequest;
import com.upuphone.cloudplatform.usercenter.auth.vo.refresh.RefreshTokenResponse;
import com.upuphone.cloudplatform.usercenter.common.util.OrikaUtil;
import com.upuphone.cloudplatform.usercenter.service.authentication.CheckAccessTokenService;
import com.upuphone.cloudplatform.usercenter.service.authentication.CheckApproveStateService;
import com.upuphone.cloudplatform.usercenter.service.authentication.GenerateAuthCodeService;
import com.upuphone.cloudplatform.usercenter.service.authentication.GenerateTokenService;
import com.upuphone.cloudplatform.usercenter.service.authentication.RefreshTokenService;
import com.upuphone.cloudplatform.usercenter.service.authentication.UserBaseInfoService;
import com.upuphone.cloudplatform.usercenter.service.authentication.model.CheckAccessTokenResult;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Api(tags = "认证API")
public class AuthenticationController implements AuthenticationClient {

    @Autowired
    private UserBaseInfoService userBaseInfoService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private CheckAccessTokenService checkAccessTokenService;
    @Autowired
    private CheckApproveStateService checkApproveStateService;
    @Autowired
    private GenerateAuthCodeService generateAuthCodeService;
    @Autowired
    private GenerateTokenService generateTokenService;

    @Override
    public CommonResponse<CheckAccessTokenResponse> checkAccessToken(CheckAccessTokenRequest request) throws Exception {
        CheckAccessTokenResult result = checkAccessTokenService.process(request);
        CheckAccessTokenResponse responseBody = OrikaUtil.map(result, CheckAccessTokenResponse.class);
        return CommonResponse.success(responseBody);
    }

    @Override
    public CommonResponse<GetUserBaseInfoResponse> getUserBaseInfo() throws Exception {
        return CommonResponse.success(userBaseInfoService.process(null));
    }

    @Override
    public CommonResponse<RefreshTokenResponse> refreshToken(@Valid RefreshTokenRequest request) {
        return CommonResponse.success(refreshTokenService.process(request));
    }

    @Override
    public CommonResponse<Void> checkLoginState() {
        // 暂时使用网关验证机制 是否选择Body中传token或者其它方式待定 暂无@Valid
        return CommonResponse.success();
    }

    @Override
    public CommonResponse<CheckApproveStateResponse> checkApproveState() {
        // 暂时将数据都放在请求头 request为空 暂无@Valid
        return CommonResponse.success(checkApproveStateService.process(null));
    }

    @Override
    public CommonResponse<String> generateAuthCode(@Valid GenerateAuthCodeRequest request) {
        GenerateAuthCodeResponse resp = generateAuthCodeService.process(request);
        // return String.format("redirect:%s?code=%s&state=%s", resp.getRedirectUri(), resp.getCode(), resp.getState());
        return CommonResponse.success(resp.getCode());
    }

    @Override
    public CommonResponse<GenerateTokenResponse> generateToken(@Valid GenerateTokenRequest request) {
        return CommonResponse.success(generateTokenService.process(request));
    }
}
