package com.upuphone.cloudplatform.usercenter.remote.flashlogin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "一键登录response")
public class FlashLoginMobileQueryResponse {

    @ApiModelProperty(value = "手机号")
    private String mobile;
}
