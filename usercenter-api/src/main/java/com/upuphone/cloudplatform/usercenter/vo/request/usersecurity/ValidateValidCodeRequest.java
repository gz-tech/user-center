package com.upuphone.cloudplatform.usercenter.vo.request.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("验证码认证request")
public class ValidateValidCodeRequest {

    @ApiModelProperty(value = "验证码", required = true)
    @NotBlank(message = "验证码不能为空")
    private String validCode;

    @ApiModelProperty(value = "类型，1-手机号，2-邮箱", required = true)
    @NotNull(message = "类型不能为空")
    private Integer type;
}