package com.upuphone.cloudplatform.usercenter.auth.vo.authcode;

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
@ApiModel("获取授权码request")
@Data
public class GenerateAuthCodeRequest {

    @ApiModelProperty(value = "appId", required = true)
    @NotBlank(message = "appId不能为空")
    private String appId;

    @ApiModelProperty(value = "redirectUri 回调地址(需提前注册)", required = true)
    @NotBlank(message = "redirectUri不能为空")
    private String redirectUri;

    @ApiModelProperty(value = "scope, 默认为最大权限", hidden = true)
    private String scope;

    @ApiModelProperty("state 防篡改 原样返回")
    private String state;
}
