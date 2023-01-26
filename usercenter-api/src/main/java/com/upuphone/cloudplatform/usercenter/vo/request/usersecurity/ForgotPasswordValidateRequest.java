package com.upuphone.cloudplatform.usercenter.vo.request.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "找回密码request")
public class ForgotPasswordValidateRequest {

    @ApiModelProperty(value = "userId")
    private String userId;
    @ApiModelProperty(value = "验证类型 1-安全手机号 2-绑定手机号 3-邮箱", required = true, allowableValues = "1,2,3")
    @NotNull
    @Range(min = 1, max = 3)
    private Integer validType;
    @ApiModelProperty(value = "验证码", required = true)
    @NotBlank(message = "验证码不能为空")
    private String validCode;
    @ApiModelProperty(value = "设备唯一ID", required = true)
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
}
