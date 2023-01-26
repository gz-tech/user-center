package com.upuphone.cloudplatform.usercenter.vo.request.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/30
 */
@Data
@ApiModel("移除设备请求")
public class RemoveDeviceRequest {

    @ApiModelProperty(value = "设备ID", required = true)
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    @ApiModelProperty(value = "列表ID", required = true)
    @NotNull(message = "列表ID不能为空")
    private Long id;
}
