package com.upuphone.cloudplatform.usercenter.vo.request.login;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.vo.jscksondeserializer.LoginTypeDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @Classname LoginUserPasswordRequest
 * @Description
 * @Date 2022/3/31 10:33 上午
 * @Created by gz-d
 */
@Getter
@Setter
@ApiModel(value = "密码登录")
public class LoginUsePasswordRequest {
    @ApiModelProperty(value = "手机号或铂星号或邮箱")
    private String account;

    @ApiModelProperty(value = "区号，type为phone_password时必填")
    private String phoneCode;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不为空")
    private String password;

    @ApiModelProperty(value = "登录类型 手机密码-phone_password  邮箱帐号密码-account_email_pwd",
            allowableValues = "phone_password, account_email_pwd", example = "phone_password")
    @JsonDeserialize(using = LoginTypeDeserializer.class)
    private LoginTypeEnum type;

    @ApiModelProperty(value = "二次校验密钥(非新设备校验登录不要传此字段)")
    private String secondLoginSecret;

    @ApiModelProperty(value = "三方绑定ticket")
    private String thirdPartyBindTicket;
}
