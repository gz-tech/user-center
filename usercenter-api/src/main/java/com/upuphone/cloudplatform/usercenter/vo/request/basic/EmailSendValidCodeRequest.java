package com.upuphone.cloudplatform.usercenter.vo.request.basic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.vo.jscksondeserializer.ValidCodeTypeDeserializer;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.CaptchaBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.EMAIL_EXP;

@Setter
@Getter
@ApiModel(value = "发送邮件验证码 request")
public class EmailSendValidCodeRequest extends CaptchaBaseRequest {

    @ApiModelProperty(value = "邮箱地址", required = true)
    @NotBlank(message = "邮箱地址不能为空")
    @Pattern(regexp = EMAIL_EXP)
    private String emailAddress;

    @JsonDeserialize(using = ValidCodeTypeDeserializer.class)
    @NotNull(message = "验证码类型不能为空")
    @ApiModelProperty(value = "验证码类型 1-注册 12-更换邮箱验证新邮箱 13-邮箱新设备验证",
            allowableValues = "1,12，13", example = "1", required = true)
    private ValidCodeType type;
}
