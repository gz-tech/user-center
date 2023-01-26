package com.upuphone.cloudplatform.usercenter.vo.request.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@ApiModel(value = "校验用户注册的验证码 request")
public class CheckPhoneCodeForRegistRequest {

    @ApiModelProperty(value = "国际区号", required = true)
    @NotBlank(message = "区号不能为空")
    private String phoneCode;

    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(value = "手机号码", required = true)
    private String phoneNumber;

    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty(value = "验证码", required = true)
    private String validCode;
}
