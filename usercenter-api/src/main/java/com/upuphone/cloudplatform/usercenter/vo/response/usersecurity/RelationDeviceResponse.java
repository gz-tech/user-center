package com.upuphone.cloudplatform.usercenter.vo.response.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description: 关联设备
 *
 * @author hanzhumeng
 * Created: 2021/12/22
 */
@Data
@ApiModel("关联设备详情")
public class RelationDeviceResponse {

    @ApiModelProperty("设备ID")
    private String deviceId;
    @ApiModelProperty("设备型号")
    private String model;
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("当前设备标志")
    private Boolean currentFlag;
}
