package com.upuphone.cloudplatform.usercenter.vo.request.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value = "三方登录请求 request")
public class ThirdPartLoginRequest {

    @ApiModelProperty("授权code")
    private String code;

    @ApiModelProperty("绑定类型 1 微信")
    private Integer bingdingType;
}
