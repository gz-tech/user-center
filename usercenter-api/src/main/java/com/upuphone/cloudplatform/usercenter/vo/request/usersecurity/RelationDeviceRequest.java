package com.upuphone.cloudplatform.usercenter.vo.request.usersecurity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Description: 获取关联设备
 *
 * @author hanzhumeng
 * Created: 2021/12/22
 */
@Data
@ApiModel("关联设备详情请求")
public class RelationDeviceRequest {

    private Long id;
}
