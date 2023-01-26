package com.upuphone.cloudplatform.usercenter.vo.response.register;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckEmailCodeForRegistResponse {
    @ApiModelProperty(value = "验证码", required = true)
    private String ticket;
}
