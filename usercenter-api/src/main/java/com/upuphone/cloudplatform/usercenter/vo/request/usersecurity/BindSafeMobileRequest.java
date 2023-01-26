package com.upuphone.cloudplatform.usercenter.vo.request.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Description: 设置安全手机号请求
 *
 * @author hanzhumeng
 * Created: 2021/12/23
 */
@Data
@ApiModel("绑定安全手机号请求")
public class BindSafeMobileRequest {

    @ApiModelProperty(value = "验证码", required = true)
    @NotBlank(message = "验证码不能为空")
    private String validCode;

    @ApiModelProperty(value = "安全手机号", required = true)
    @NotBlank(message = "安全手机号不能为空")
    private String telePhoneNumber;

    @ApiModelProperty(value = "手机区号", required = true)
    @NotBlank(message = "手机区号不能为空")
    private String telephoneCode;
}
