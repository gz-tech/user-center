package com.upuphone.cloudplatform.usercenter.remote.flashlogin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel(value = "一键登录request")
public class FlashLoginMobileQueryRequest {

    @NotBlank(message = "一键登录token不为空")
    @ApiModelProperty(value = "闪验SDK token", required = true)
    private String token;
}
