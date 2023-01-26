package com.upuphone.cloudplatform.usercenter.auth.vo.extendtokenvalid;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "Extend valid time for Access Token request")
public class ExtendVlidAccTokenRequest {

    @ApiModelProperty(value = "accessToken", required = true)
    private String accessToken;
}
