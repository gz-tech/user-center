package com.upuphone.cloudplatform.usercenter.auth.vo.extendtokenvalid;

import com.upuphone.cloudplatform.common.model.mobile.UserInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@ApiModel(value = "Extend valid time for Access Token response")
public class ExtendVlidAccTokenResponse {

    @ApiModelProperty(value = "accessToken", required = true)
    private String accessToken;

    @ApiModelProperty(value = "expirationTime", required = true)
    private LocalDateTime expirationTime;

    @ApiModelProperty(value = "startValidTime", required = true)
    private LocalDateTime startValidTime;

    @ApiModelProperty(value = "userInfo", required = true)
    private UserInfo userInfo;
}
