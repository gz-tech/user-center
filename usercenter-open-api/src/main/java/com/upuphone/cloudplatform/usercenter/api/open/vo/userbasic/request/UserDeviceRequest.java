package com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ApiModel("用户登录设备 request")
public class UserDeviceRequest {

    @ApiModelProperty(value = "用户id")
    @NotEmpty
    private String userId;
}
