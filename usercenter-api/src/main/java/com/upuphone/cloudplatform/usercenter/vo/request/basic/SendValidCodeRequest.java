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

@Setter
@Getter
@ApiModel(value = "发送验证码 request")
public class SendValidCodeRequest extends CaptchaBaseRequest {

    @ApiModelProperty(value = "国际区号", required = true)
    @NotBlank(message = "国际区号不能为空")
    private String phoneCode;

    @ApiModelProperty(value = "手机号码", required = true)
    @NotBlank(message = "手机号码不能为空")
    private String phoneNumber;

    @JsonDeserialize(using = ValidCodeTypeDeserializer.class)
    @NotNull(message = "验证码类型不能为空")
    @ApiModelProperty(value = "验证码类型 1-注册 2-登录 "
            + "4-修改绑定手机验证新绑定手机, "
            + "7-修改安全手机验证新安全手机, "
            + "15-注册登录简易流程",
            allowableValues = "1,2,4,7", example = "1", required = true)
    private ValidCodeType type;

    @ApiModelProperty(value = "三方绑定登录时，发验证码需要填")
    private Integer thirdBindType;
}
