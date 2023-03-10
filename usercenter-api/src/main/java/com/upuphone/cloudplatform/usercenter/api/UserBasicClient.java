package com.upuphone.cloudplatform.usercenter.api;

import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.vo.request.DeleteAccountRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.UpdateUserInfoRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.AccountBindingRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.AccountUnbindingRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.login.FlashLoginRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUsePasswordRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUseValidCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.logout.LogoutRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.logout.WebLogoutRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.register.SimpleRegisterLoginRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.GetUserDetailResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.AccountBindingListResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.AccountBindingResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.AccountUnbindingResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.logout.LogoutResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.logout.WebLogoutResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(tags = "?????????????????? API")
@FeignClient(name = "user-center", contextId = "user-basic")
public interface UserBasicClient {


    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "???????????????", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "??????????????????????????????", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[????????????] userId"),
            @ApiImplicitParam(paramType = "header", name = "X-refresh-token-id", value = "[????????????]")
    })
    @PostMapping("/logout")
    @ResponseBody
    CommonResponse<LogoutResponse> logout(@RequestBody LogoutRequest request);

    @ApiOperation("web????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "???????????????", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "??????????????????????????????", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[????????????] userId"),
            @ApiImplicitParam(paramType = "header", name = "X-refresh-token-id", value = "[????????????]")
    })
    @PostMapping("/web-logout")
    @ResponseBody
    CommonResponse<WebLogoutResponse> webLogout(@RequestBody WebLogoutRequest request);

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "???????????????", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "??????????????????????????????", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[????????????] userId")
    })
    @DeleteMapping("/delete-account")
    @ResponseBody
    CommonResponse deleteAccount(@RequestBody DeleteAccountRequest request);

    @ApiOperation("???????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-app-id", value = "appId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-model", value = "model", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-type", value = "deviceType", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-name", value = "deviceName", required = true)
    })
    @PostMapping("/login/use-valid-code")
    @ResponseBody
    CommonResponse<LoginResponse> loginUseValidCode(@RequestBody LoginUseValidCodeRequest request);

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "???????????????", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "??????????????????????????????", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-app-id", value = "appId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-model", value = "model", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-type", value = "deviceType", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-name", value = "deviceName", required = true)
    })
    @PostMapping("/login/use-password")
    @ResponseBody
    CommonResponse<LoginResponse> loginUsePassword(@RequestBody LoginUsePasswordRequest request);

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-app-id", value = "appId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-model", value = "model", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-type", value = "deviceType", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-name", value = "deviceName", required = true)
    })
    @PostMapping("/login/flash-login")
    @ResponseBody
    CommonResponse<LoginResponse> flashLogin(@RequestBody FlashLoginRequest request);

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[????????????] userId")
    })
    @GetMapping("/user-info/detail")
    @ResponseBody
    CommonResponse<GetUserDetailResponse> userDetail();

    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[????????????] userId")
    })
    @PostMapping("/user-info/update")
    @ResponseBody
    CommonResponse updateUserInfo(@RequestBody UpdateUserInfoRequest request);


    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[????????????] userId")
    })
    @GetMapping("/user-info/binding-list")
    @ResponseBody
    CommonResponse<AccountBindingListResponse> accountBindingList();


    @ApiOperation("??????")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[????????????] userId")
    })
    @PostMapping("/user-info/binding")
    @ResponseBody
    CommonResponse<AccountBindingResponse> accountBinding(@RequestBody AccountBindingRequest request);


    @ApiOperation("??????")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[????????????] userId")
    })
    @PostMapping("/user-info/unbinding")
    @ResponseBody
    CommonResponse<AccountUnbindingResponse> accountUnbinding(@RequestBody AccountUnbindingRequest request);

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-app-id", value = "appId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-model", value = "model", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-type", value = "deviceType", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-name", value = "deviceName", required = true)
    })
    @PostMapping("/login/third-part")
    @ResponseBody
    CommonResponse<LoginResponse> thirdPartLogin(@RequestBody AccountBindingRequest request);

    @ApiOperation(value = "??????????????????")
    @PostMapping("/register/login/simple")
    CommonResponse<LoginResponse> simpleRegisterLogin(@RequestBody SimpleRegisterLoginRequest request);
}
