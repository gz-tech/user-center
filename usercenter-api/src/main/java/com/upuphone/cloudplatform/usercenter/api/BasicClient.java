package com.upuphone.cloudplatform.usercenter.api;

import com.upuphone.cloudplatform.common.annotation.CheckHeader;
import com.upuphone.cloudplatform.common.annotation.CheckHeaders;
import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.*;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "基础功能 API")
@FeignClient(name = "user-center", contextId = "basic")
public interface BasicClient {

    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
    })
    @CheckHeaders({
            @CheckHeader("X-device-id")
    })
    @ApiOperation(value = "发送邮件验证码")
    @PostMapping("/email/send-valid-code")
    @ResponseBody
    CommonResponse<EmailSendValidCodeResponse> emailSendValidCode(@RequestBody EmailSendValidCodeRequest request);

    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
    })
    @CheckHeaders({
            @CheckHeader("X-device-id")
    })
    @ApiOperation(value = "无邮箱地址发送邮件验证码")
    @PostMapping("/email/send-valid-code-without-address")
    @ResponseBody
    CommonResponse<EmailSendValidCodeWithoutAddressResponse> emailSendValidCodeWithoutAddress(
            @RequestBody EmailSendValidCodeWithoutAddressRequest request);


    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
    })
    @CheckHeaders({
            @CheckHeader("X-device-id")
    })
    @ApiOperation(value = "发送验证码")
    @PostMapping("/send-valid-code")
    @ResponseBody
    CommonResponse<SendValidCodeResponse> sendValidCode(@RequestBody SendValidCodeRequest request);

    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
    })
    @CheckHeaders({
            @CheckHeader("X-device-id")
    })
    @ApiOperation(value = "根据用户绑定手机类型发送验证码，前端无需传手机号")
    @PostMapping("/send-valid-code-without-phone-number")
    @ResponseBody
    CommonResponse<Boolean> sendValidCodeWithoutPhone(@RequestBody SendValidCodeWithoutPhoneRequest request);

    @ApiOperation("获取所有的电话区号")
    @GetMapping("/phone-area-code")
    @ResponseBody
    CommonResponse<PhoneAreaCodeResponse> phoneAreaCode();

    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
    })
    @PostMapping(value = "/upload-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "上传用户头像")
    CommonResponse<UpLoadPhotoResponse> uploadPhoto(@RequestPart("file") MultipartFile multipartFile);

    @ApiOperation("获取全量国家")
    @GetMapping("/country/list")
    @ResponseBody
    CommonResponse<CountryListResponse> queryCountryList();

    @ApiOperation("判断appid是否使用公有token")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-app-id", value = "appId", required = true)
    })
    @CheckHeaders({
            @CheckHeader("X-app-id")
    })
    @GetMapping("/judge-oauth-client")
    @ResponseBody
    CommonResponse<Boolean> isOauthAppId();

    @ApiOperation(value = "判断是否简易登录注册流程")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-app-id", value = "appId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-model", value = "model", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-type", value = "deviceType", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-name", value = "deviceName", required = true)
    })
    @GetMapping("/register/mode")
    CommonResponse<String> isSimpleRegister();

}
