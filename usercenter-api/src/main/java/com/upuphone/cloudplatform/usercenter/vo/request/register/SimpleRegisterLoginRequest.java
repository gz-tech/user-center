package com.upuphone.cloudplatform.usercenter.vo.request.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "注册短信登录合一接口request")
@Data
public class SimpleRegisterLoginRequest {
    @ApiModelProperty(value = "国际区号", required = true)
    @NotBlank(message = "区号不能为空")
    private String phoneCode;

    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(value = "手机号码", required = true)
    private String phoneNumber;

    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty(value = "验证码", required = true)
    private String validCode;

    @ApiModelProperty(value = "三方绑定ticket")
    private String thirdPartyBindTicket;
}
