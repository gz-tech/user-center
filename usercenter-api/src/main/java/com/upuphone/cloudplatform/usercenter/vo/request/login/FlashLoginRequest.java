package com.upuphone.cloudplatform.usercenter.vo.request.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @Classname FlashLoginRequest
 * @Description
 * @Date 2022/1/24 7:43 下午
 * @Created by gz-d
 */
@Getter
@Setter
@ApiModel(value = "一键登录request")
public class FlashLoginRequest {

    @NotBlank(message = "一键登录token不为空")
    @ApiModelProperty(value = "闪验SDK token", required = true)
    private String token;

    @ApiModelProperty(value = "三方绑定ticket")
    private String thirdPartyBindTicket;

    @ApiModelProperty(value = "登录模式，简化注册登录-simple，正常流程-normal")
    private String loginMode;

    @ApiModelProperty(value = "滑块验证")
    private String validate;
}
