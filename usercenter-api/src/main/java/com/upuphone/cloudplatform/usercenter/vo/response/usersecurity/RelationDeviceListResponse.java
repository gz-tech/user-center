package com.upuphone.cloudplatform.usercenter.vo.response.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Description: 关联设备列表
 *
 * @author hanzhumeng
 * Created: 2021/12/22
 */
@Data
@ApiModel
public class RelationDeviceListResponse {

    @ApiModelProperty("设备列表")
    List<RelationDeviceInfo> deviceList;
}
