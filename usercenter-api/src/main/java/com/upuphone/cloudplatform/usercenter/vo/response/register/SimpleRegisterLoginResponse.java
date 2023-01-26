package com.upuphone.cloudplatform.usercenter.vo.response.register;

import com.upuphone.cloudplatform.usercenter.vo.SimpleUserInfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "注册短信登录合一接口response")
@Data
@NoArgsConstructor
public class SimpleRegisterLoginResponse {
    @ApiModelProperty(value = "accessToken", required = true)
    private String accessToken;

    @ApiModelProperty(value = "refreshToken", required = true)
    private String refreshToken;

    @ApiModelProperty(value = "验证码", required = true)
    private String ticket;

    @ApiModelProperty(value = "用户基本信息")
    private SimpleUserInfoVo simpleUserInfoVo;
}
