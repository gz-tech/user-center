package com.upuphone.cloudplatform.usercenter.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@ApiModel("设备信息")
public class DeviceInfoVo {

    @ApiModelProperty(value = "id")
    private String deviceId;

    @ApiModelProperty(value = "型号")
    private String model;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备类型0-PC, 1-手机,2-平板,3-移动web,4-car,5-vr,-1=未知")
    private Integer deviceType;

    @ApiModelProperty(value = "登录时间")
    private LocalDateTime loginTime;

    @ApiModelProperty(value = "设是否历史")
    private Boolean isLogin;
}
