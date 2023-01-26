package com.upuphone.cloudplatform.usercenter.vo.request.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description:获取绑定手机号及安全手机号请求
 *
 * @author hanzhumeng
 * Created: 2022/1/5
 */
@Data
@ApiModel("获取手机号及邮箱请求")
public class MobileAndEmailRequest {

    @ApiModelProperty(value = "铂星ID")
    private String boxingId;

    @ApiModelProperty(value = "绑定手机号, 如果铂星ID未传则校验该值")
    private String mobile;

    @ApiModelProperty(value = "区号， 默认86")
    private String areaCode;

    @ApiModelProperty(value = "邮箱，如果铂星ID和绑定手机号均未传将校验该值")
    private String email;
}
