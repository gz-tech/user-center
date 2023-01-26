package com.upuphone.cloudplatform.usercenter.auth.api;

import com.upuphone.cloudplatform.common.annotation.CheckHeader;
import com.upuphone.cloudplatform.common.annotation.CheckHeaders;
import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.auth.vo.authcode.GenerateAuthCodeRequest;
import com.upuphone.cloudplatform.usercenter.auth.vo.checkapprovestate.CheckApproveStateResponse;
import com.upuphone.cloudplatform.usercenter.auth.vo.checktoken.CheckAccessTokenRequest;
import com.upuphone.cloudplatform.usercenter.auth.vo.checktoken.CheckAccessTokenResponse;
import com.upuphone.cloudplatform.usercenter.auth.vo.generatetoken.GenerateTokenRequest;
import com.upuphone.cloudplatform.usercenter.auth.vo.generatetoken.GenerateTokenResponse;
import com.upuphone.cloudplatform.usercenter.auth.vo.getuserbaseinfo.GetUserBaseInfoResponse;
import com.upuphone.cloudplatform.usercenter.auth.vo.refresh.RefreshTokenRequest;
import com.upuphone.cloudplatform.usercenter.auth.vo.refresh.RefreshTokenResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "user-center", contextId = "authentication")
public interface AuthenticationClient {

    @PostMapping("/authentication/check-token")
    @ResponseBody
    CommonResponse<CheckAccessTokenResponse> checkAccessToken(@RequestBody CheckAccessTokenRequest request) throws Exception;

    @PostMapping("/authentication/get-user-baseinfo")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @ResponseBody
    CommonResponse<GetUserBaseInfoResponse> getUserBaseInfo() throws Exception;

    @ApiOperation("刷新accessToken")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-app-id", value = "appId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-model", value = "设备型号", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "设备ID", required = true)
    })
    @PostMapping("/authentication/refresh")
    @ResponseBody
    CommonResponse<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request);

    @ApiOperation("判断用户帐号域登录态")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    @GetMapping("/authentication/login-state")
    @ResponseBody
    CommonResponse<Void> checkLoginState();

    @ApiOperation("判断用户授权状态")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-app-id", value = "appId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "设备ID", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-type", value = "设备类型", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-app-id"),
            @CheckHeader("X-device-id"),
            @CheckHeader("X-device-type"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    @GetMapping("/authentication/approve-state")
    @ResponseBody
    CommonResponse<CheckApproveStateResponse> checkApproveState();

    @ApiOperation("获取用户授权码, 该接口会触发重定向")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId"),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-app-id", value = "appId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-model", value = "model", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-type", value = "deviceType", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-name", value = "deviceName", required = true)
    })
    @CheckHeaders({
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    @GetMapping("/authentication/auth-code")
    CommonResponse<String> generateAuthCode(GenerateAuthCodeRequest request);

    @ApiOperation("生成token")
    @PostMapping("/authentication/token")
    @ResponseBody
    CommonResponse<GenerateTokenResponse> generateToken(@RequestBody GenerateTokenRequest request);
}
