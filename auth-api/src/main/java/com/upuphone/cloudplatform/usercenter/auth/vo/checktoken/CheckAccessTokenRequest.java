package com.upuphone.cloudplatform.usercenter.auth.vo.checktoken;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "CheckAccessTokenRequest")
public class CheckAccessTokenRequest {

    @ApiModelProperty(value = "accessToken", required = true)
    private String accessToken;
}
