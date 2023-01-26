package com.upuphone.cloudplatform.usercenter.vo.request.logout;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author guangzheng.ding
 * @date 2021/12/16 13:42
 */
@Getter
@Setter
@ApiModel(value = "登出")
public class LogoutRequest {
    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不为空")
    private String password;
}
