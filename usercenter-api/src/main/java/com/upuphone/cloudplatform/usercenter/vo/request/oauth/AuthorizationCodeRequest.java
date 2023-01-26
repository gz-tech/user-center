package com.upuphone.cloudplatform.usercenter.vo.request.oauth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "获取AuthorizationCode Request")
public class AuthorizationCodeRequest {
    @ApiModelProperty(value = "用户ID", required = true)
    private String userId;
    @ApiModelProperty(value = "appID", required = true)
    private String appId;
    @ApiModelProperty(value = "accessToken", required = true)
    private String accessToken;
}
