package com.upuphone.cloudplatform.usercenter.vo.request.login;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.vo.jscksondeserializer.LoginTypeDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Classname NewDeviceLoginUserValidCodeRequest
 * @Description
 * @Date 2022/2/11 5:15 下午
 * @Created by gz-d
 */
@Getter
@Setter
@ApiModel(value = "新设备登陆二次校验--邮箱 手机 安全手机")
public class NewDeviceLoginUseValidCodeRequest {
    @ApiModelProperty(value = "用户id", required = true)
    @NotNull(message = "用户id不为空")
    private Long userId;

    @ApiModelProperty(value = "验证码", required = true)
    @NotBlank(message = "验证码不为空")
    private String validCode;

    @ApiModelProperty(value = "二次校验凭证", required = true)
    @NotBlank(message = "二次校验凭证不为空")
    private String secondLoginSecret;

    @ApiModelProperty(value = "二次校验登录类型", required = true)
    @NotNull(message = "登录类型")
    @JsonDeserialize(using = LoginTypeDeserializer.class)
    private LoginTypeEnum loginType;
}
