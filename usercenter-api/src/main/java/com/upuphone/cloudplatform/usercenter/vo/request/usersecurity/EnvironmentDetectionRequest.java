package com.upuphone.cloudplatform.usercenter.vo.request.usersecurity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.upuphone.cloudplatform.usercenter.constants.CaptchaBusinessEnum;
import com.upuphone.cloudplatform.usercenter.vo.jscksondeserializer.CaptchaBusinessTypeDeserializer;
import com.upuphone.cloudplatform.usercenter.vo.jscksondeserializer.LoginTypeDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 环境检测
 */
@Data
@ApiModel("是否需要环境检测请求")
public class EnvironmentDetectionRequest {
    @ApiModelProperty(value = "业务类型：安全中心模块-security；注册模块-register", required = true)
    @JsonDeserialize(using = CaptchaBusinessTypeDeserializer.class)
    private CaptchaBusinessEnum captchaBusinessType;

    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    @ApiModelProperty(value = "手机号国家码")
    private String phoneCode;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "userId")
    private String userId;
}
