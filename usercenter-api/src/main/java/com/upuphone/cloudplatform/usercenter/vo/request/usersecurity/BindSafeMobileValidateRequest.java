package com.upuphone.cloudplatform.usercenter.vo.request.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/27
 */
@Data
@ApiModel("绑定安全手机号验证密码请求")
public class BindSafeMobileValidateRequest {

    @ApiModelProperty(value = "验证密码", required = true)
    @NotBlank
    private String password;

    @ApiModelProperty(value = "滑块验证码")
    private String validate;
}
