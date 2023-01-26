package com.upuphone.cloudplatform.usercenter.vo.request.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Classname NewDeviceLoginUsePasswordRequest
 * @Description
 * @Date 2022/2/11 3:55 下午
 * @Created by gz-d
 */
@Getter
@Setter
@ApiModel(value = "新设备登陆二次校验--使用密码登陆")
public class NewDeviceLoginUsePasswordRequest {

    @ApiModelProperty(value = "用户id，第一次登陆时会返回给客户端", required = true)
    @NotNull(message = "用户id不为空")
    private Long userId;

    @ApiModelProperty(value = "密码不为空", required = true)
    @NotBlank(message = "密码不为空")
    private String password;

    @ApiModelProperty(value = "二次校验密钥，第一次登陆时会返回给客户端", required = true)
    @NotBlank(message = "二次校验凭证不为空")
    private String secondLoginSecret;
}
