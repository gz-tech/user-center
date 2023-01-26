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

@Api(tags = "用户基础功能 API")
@FeignClient(name = "user-center", contextId = "user-basic")
public interface UserBasicClient {


    @ApiOperation("用户登出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId"),
            @ApiImplicitParam(paramType = "header", name = "X-refresh-token-id", value = "[前端不传]")
    })
    @PostMapping("/logout")
    @ResponseBody
    CommonResponse<LogoutResponse> logout(@RequestBody LogoutRequest request);

    @ApiOperation("web用户登出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId"),
            @ApiImplicitParam(paramType = "header", name = "X-refresh-token-id", value = "[前端不传]")
    })
    @PostMapping("/web-logout")
    @ResponseBody
    CommonResponse<WebLogoutResponse> webLogout(@RequestBody WebLogoutRequest request);

    @ApiOperation("帐号注销")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @DeleteMapping("/delete-account")
    @ResponseBody
    CommonResponse deleteAccount(@RequestBody DeleteAccountRequest request);

    @ApiOperation("验证码登录")
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

    @ApiOperation("密码登录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-app-id", value = "appId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-model", value = "model", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-type", value = "deviceType", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-name", value = "deviceName", required = true)
    })
    @PostMapping("/login/use-password")
    @ResponseBody
    CommonResponse<LoginResponse> loginUsePassword(@RequestBody LoginUsePasswordRequest request);

    @ApiOperation("一键登录")
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

    @ApiOperation("用户详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @GetMapping("/user-info/detail")
    @ResponseBody
    CommonResponse<GetUserDetailResponse> userDetail();

    @ApiOperation("更新用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @PostMapping("/user-info/update")
    @ResponseBody
    CommonResponse updateUserInfo(@RequestBody UpdateUserInfoRequest request);


    @ApiOperation("查询绑定关系")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @GetMapping("/user-info/binding-list")
    @ResponseBody
    CommonResponse<AccountBindingListResponse> accountBindingList();


    @ApiOperation("绑定")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @PostMapping("/user-info/binding")
    @ResponseBody
    CommonResponse<AccountBindingResponse> accountBinding(@RequestBody AccountBindingRequest request);


    @ApiOperation("解绑")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @PostMapping("/user-info/unbinding")
    @ResponseBody
    CommonResponse<AccountUnbindingResponse> accountUnbinding(@RequestBody AccountUnbindingRequest request);

    @ApiOperation("三方登录")
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

    @ApiOperation(value = "简易流程登录")
    @PostMapping("/register/login/simple")
    CommonResponse<LoginResponse> simpleRegisterLogin(@RequestBody SimpleRegisterLoginRequest request);
}
