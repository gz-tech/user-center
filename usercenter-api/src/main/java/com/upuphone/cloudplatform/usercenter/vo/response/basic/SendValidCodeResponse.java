package com.upuphone.cloudplatform.usercenter.vo.response.basic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "发送验证码 response")
public class SendValidCodeResponse {
    @ApiModelProperty(value = "发送目标号码", required = true)
    private String telePhoneNumber;
}
