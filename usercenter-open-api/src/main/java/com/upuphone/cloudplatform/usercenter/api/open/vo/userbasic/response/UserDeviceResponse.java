package com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.response;

import com.upuphone.cloudplatform.usercenter.api.vo.DeviceInfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("用户登录设备 response")
public class UserDeviceResponse {

    @ApiModelProperty(value = "用户登录设备")
    private List<DeviceInfoVo> devices;
}
