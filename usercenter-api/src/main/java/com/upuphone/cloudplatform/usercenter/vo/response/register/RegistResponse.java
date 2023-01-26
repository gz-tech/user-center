package com.upuphone.cloudplatform.usercenter.vo.response.register;

import com.upuphone.cloudplatform.usercenter.vo.SimpleUserInfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "用户注册 response")
public class RegistResponse {
    @ApiModelProperty(value = "鉴权accessToken", required = true)
    private String accessToken;

    @ApiModelProperty(value = "refreshToken", required = true)
    private String refreshToken;

    @ApiModelProperty(value = "userId")
    private Long userId;

    @ApiModelProperty(value = "用户信息", required = true)
    private SimpleUserInfoVo simpleUserInfoVo;
}
