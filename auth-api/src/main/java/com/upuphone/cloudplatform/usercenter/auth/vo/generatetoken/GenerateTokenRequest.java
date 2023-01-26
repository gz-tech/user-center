package com.upuphone.cloudplatform.usercenter.auth.vo.generatetoken;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/26
 */
@ApiModel("生成Token request")
@Data
public class GenerateTokenRequest {

    @ApiModelProperty(value = "授权类型 固定 authorization_code", required = true)
    @NotBlank(message = "grantType不能为空")
    private String grantType;

    @ApiModelProperty(value = "appId", required = true)
    @NotBlank(message = "appId不能为空")
    private String appId;

    @ApiModelProperty(value = "app密钥", required = true)
    @NotBlank(message = "secret不能为空")
    private String secret;

    @ApiModelProperty(value = "授权码", required = true)
    @NotBlank(message = "code不能为空")
    private String code;
}
