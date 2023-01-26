package com.upuphone.cloudplatform.usercenter.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author guangzheng.ding
 * @date 2021/12/23 9:49
 */
@Getter
@Setter
public class DeleteAccountRequest {
    @NotBlank(message = "密码不为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;
}
