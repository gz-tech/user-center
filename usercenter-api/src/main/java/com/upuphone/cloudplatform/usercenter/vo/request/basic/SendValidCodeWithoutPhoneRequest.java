package com.upuphone.cloudplatform.usercenter.vo.request.basic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.vo.jscksondeserializer.ValidCodeTypeDeserializer;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.CaptchaBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/25
 */
@Data
@ApiModel("根据用户ID和绑定号码类型发送验证码")
public class SendValidCodeWithoutPhoneRequest extends CaptchaBaseRequest {

    @ApiModelProperty(value = "userId", required = true)
    @NotNull(message = "userId不能为空")
    private Long userId;

    @ApiModelProperty(value = "号码类型 1-安全手机号 2-绑定手机号", required = true)
    @NotNull(message = "号码类型不能为空")
    private Integer phoneType;

    @JsonDeserialize(using = ValidCodeTypeDeserializer.class)
    @NotNull(message = "验证码类型不能为空")
    @ApiModelProperty(value = "验证码类型 " +
            "5-修改绑定手机验证当前安全手机/绑定手机, " +
            "6-修改安全手机验证当前安全手机/绑定手机, " +
            "8-忘记密码验证当前安全手机/绑定手机, " +
            "9-安全手机号登录, " +
            "10-新设备登录, " +
            "11-更换邮箱验证当前安全手机/绑定手机" +
            "14-sdk验证码校验",
            allowableValues = "5,6,8,9,10,11,14", example = "14", required = true)
    private ValidCodeType type;
}
