package com.upuphone.cloudplatform.usercenter.vo.response.login;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upuphone.cloudplatform.common.response.ErrorCode;
import com.upuphone.cloudplatform.usercenter.vo.SimpleUserInfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author guangzheng.ding
 * @date 2021/12/14 16:23
 */
@Getter
@Setter
@ApiModel(value = "登录返response")
public class LoginResponse {
    @ApiModelProperty(value = "accessToken", required = true)
    private String accessToken;

    @ApiModelProperty(value = "refreshToken", required = true)
    private String refreshToken;

    @ApiModelProperty(value = "新设备第一次登录成功时返回")
    private String secondLoginSecret;

    @ApiModelProperty(value = "注册票据/三方绑定票据")
    private String ticket;

    @ApiModelProperty(value = "帐号绑定手机号")
    private String phoneNumber;

    @ApiModelProperty(value = "帐号绑定安全手机号")
    private String securityPhoneNumber;

    @ApiModelProperty(value = "绑定邮箱")
    private String emailAddress;

    @ApiModelProperty(value = "用户基本信息")
    private SimpleUserInfoVo simpleUserInfoVo;

    @JsonIgnore
    private ErrorCode errorCode;

    public LoginResponse() {
    }

    public LoginResponse(String accessToken, String refreshToken, SimpleUserInfoVo simpleUserInfoVo) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.simpleUserInfoVo = simpleUserInfoVo;
    }
}
