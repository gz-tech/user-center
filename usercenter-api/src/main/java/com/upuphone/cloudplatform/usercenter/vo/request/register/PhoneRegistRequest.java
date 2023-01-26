package com.upuphone.cloudplatform.usercenter.vo.request.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.PWD_EXP;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.PWD_EXP_MSG;

@Setter
@Getter
@ApiModel(value = "用户手机号注册 request")
public class PhoneRegistRequest {

    @ApiModelProperty(value = "国际区号", required = true)
    @NotBlank(message = "区号不能为空")
    private String phoneCode;

    @NotBlank(message = "手机号不为空")
    @ApiModelProperty(value = "手机号码", required = true)
    private String phoneNumber;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    @Pattern(regexp = PWD_EXP, message = PWD_EXP_MSG)
    private String password;

    @ApiModelProperty(value = "用于关联授权后的三方帐户信息", required = false)
    private String thirdPartAuthTicket;
}
