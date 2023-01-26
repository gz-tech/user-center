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
@ApiModel("重置密码验证请求")
@Data
public class ResetPasswordValidRequest {

    @ApiModelProperty(value = "原密码", required = true)
    @NotBlank(message = "原密码不能为空")
    private String originPwd;

    @ApiModelProperty(value = "二次校验")
    private String validate;
}
