package com.upuphone.cloudplatform.usercenter.vo.request.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 环境检测
 */
@ApiModel(value = "环境检测验证码")
@Getter
@Setter
public class CaptchaBaseRequest {
    @ApiModelProperty(value = "环境检测验证数据")
    private String validate;

    @ApiModelProperty(value = "环境检测验证码id")
    private String captchaId;
}
