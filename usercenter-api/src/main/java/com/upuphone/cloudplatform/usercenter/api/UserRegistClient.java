package com.upuphone.cloudplatform.usercenter.api;

import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.vo.request.register.CheckEmailCodeForRegistRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.register.CheckPhoneCodeForRegistRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.register.EmailRegistRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.register.PhoneRegistRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.register.CheckEmailCodeForRegistResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.register.CheckPhoneCodeForRegistResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.register.RegistResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(tags = "用户注册 API")
@FeignClient(name = "user-center", contextId = "user-regist")
public interface UserRegistClient {

    @ApiOperation(value = "【手机号注册】校验验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true)
    })
    @PostMapping("/register/check-valid-code")
    @ResponseBody
    CommonResponse<CheckPhoneCodeForRegistResponse> checkCodeForRegister(@RequestBody CheckPhoneCodeForRegistRequest request);


    @ApiOperation("【手机号注册】设置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-app-id", value = "appId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-model", value = "设备型号", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-session-ticket", value = "ticket", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-type", value = "deviceType", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-name", value = "deviceName", required = true)
    })
    @PostMapping("/register")
    @ResponseBody
    CommonResponse<RegistResponse> register(@RequestBody PhoneRegistRequest request);


    @ApiOperation(value = "【邮箱注册】校验验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true)
    })
    @PostMapping("/register/check-email-valid-code")
    @ResponseBody
    CommonResponse<CheckEmailCodeForRegistResponse> checkEmailCodeForRegist(@RequestBody CheckEmailCodeForRegistRequest request);

    @ApiOperation("【邮箱注册】设置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-app-id", value = "appId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-model", value = "设备型号", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-type", value = "deviceType", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-name", value = "deviceName", required = true)
    })
    @PostMapping("/email-regist")
    @ResponseBody
    CommonResponse<RegistResponse> emailRegist(@RequestBody EmailRegistRequest request);
}
