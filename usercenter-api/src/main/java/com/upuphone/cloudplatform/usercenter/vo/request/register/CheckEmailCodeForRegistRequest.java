package com.upuphone.cloudplatform.usercenter.vo.request.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@ApiModel(value = "校验用户注册的验证码 request")
public class CheckEmailCodeForRegistRequest {

    @ApiModelProperty(value = "邮箱地址", required = true)
    @NotBlank(message = "邮箱地址不能为空")
    private String emailAddress;

    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty(value = "验证码", required = true)
    private String validCode;
}
