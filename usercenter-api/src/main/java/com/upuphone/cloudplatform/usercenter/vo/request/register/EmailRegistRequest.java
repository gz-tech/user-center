package com.upuphone.cloudplatform.usercenter.vo.request.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.PWD_EXP;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.PWD_EXP_MSG;

@Getter
@Setter
@ApiModel(value = "用户邮箱注册 request")
public class EmailRegistRequest {

    @NotBlank(message = "邮箱地址不为空")
    @ApiModelProperty(value = "邮箱地址", required = true)
    private String emailAddress;

    @NotBlank(message = "邮箱校验票不为空")
    @ApiModelProperty(value = "邮箱校验票", required = true)
    private String emailAddressCheckTicket;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    @Pattern(regexp = PWD_EXP, message = PWD_EXP_MSG)
    private String password;

    @ApiModelProperty(value = "用于关联授权后的三方帐户信息", required = false)
    private String thirdPartAuthTicket;
}
