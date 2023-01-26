package com.upuphone.cloudplatform.usercenter.vo.response.basic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "发送邮箱验证码 response")
public class EmailSendValidCodeResponse {
    @ApiModelProperty(value = "目标邮箱地址", required = true)
    private String emailAddress;
}
