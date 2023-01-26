package com.upuphone.cloudplatform.usercenter.vo.response.usersecurity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/30
 */
@ApiModel("关联设备信息")
@Data
public class RelationDeviceInfo {

    @ApiModelProperty("设备名称")
    private String deviceName;
    @ApiModelProperty("设备ID")
    private String deviceId;
    @ApiModelProperty("设备型号")
    private String model;
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("当前设备标志")
    private Boolean currentFlag;
    @ApiModelProperty("设备类型")
    private Integer deviceType;
    @ApiModelProperty("上次登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginTime;
}
