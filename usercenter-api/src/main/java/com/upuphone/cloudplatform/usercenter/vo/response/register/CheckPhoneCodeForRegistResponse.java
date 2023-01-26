package com.upuphone.cloudplatform.usercenter.vo.response.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value = "校验用户注册的验证码 request")
public class CheckPhoneCodeForRegistResponse {

    @ApiModelProperty(value = "验证码", required = true)
    private String ticket;
}
