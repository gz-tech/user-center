package com.upuphone.cloudplatform.usercenter.vo.request.login;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.vo.jscksondeserializer.LoginTypeDeserializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginUseValidCodeRequest {
    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    @ApiModelProperty(value = "验证码", required = true)
    @NotBlank(message = "验证码不为空")
    private String validCode;

    @ApiModelProperty(value = "手机国家区号")
    private String phoneCode;

    @ApiModelProperty(value = "二次校验凭证(非新设备校验登录不要传此字段)")
    private String secondLoginSecret;

    @ApiModelProperty(value = "二次校验登录类型(非新设备校验登录不要传此字段), 邮箱二次验证-newdevice_email," +
            " 安全手机号二次验证-security_phone_validcode, 原手机号二次验证-newdevice_validcode",
            allowableValues = "newdevice_email, security_phone_validcode, newdevice_validcode", example = "newdevice_validcode")
    @JsonDeserialize(using = LoginTypeDeserializer.class)
    private LoginTypeEnum loginType;

    @ApiModelProperty(value = "三方绑定ticket")
    private String thirdPartyBindTicket;
}
