package com.upuphone.cloudplatform.usercenter.vo.request.basic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.vo.jscksondeserializer.ValidCodeTypeDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/4
 */
@Data
@ApiModel("无邮箱地址发送验证码请求")
public class EmailSendValidCodeWithoutAddressRequest {

    @ApiModelProperty(value = "userId", required = true)
    @NotNull(message = "userId不能为空")
    private Long userId;

    @JsonDeserialize(using = ValidCodeTypeDeserializer.class)
    @NotNull(message = "验证码类型不能为空")
    @ApiModelProperty(value = "验证码类型 " +
            "5-修改绑定手机验证当前邮箱, " +
            "6-修改安全手机验证当前邮箱, " +
            "8-忘记密码验证当前邮箱, " +
            "11-更换邮箱验证当前邮箱, " +
            "13-新设备邮箱验证码登录" +
            "14-sdk验证码校验",
            allowableValues = "5,6,8,11,13,14", example = "14", required = true)
    private ValidCodeType type;

    @ApiModelProperty(value = "二次校验")
    private String validate;
}
