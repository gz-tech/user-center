package com.upuphone.cloudplatform.usercenter.vo.response.basic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/4
 */
@Data
@ApiModel("无邮箱地址发送验证码返回")
public class EmailSendValidCodeWithoutAddressResponse {

    @ApiModelProperty(value = "目标邮箱地址", required = true)
    private String emailAddress;
}
