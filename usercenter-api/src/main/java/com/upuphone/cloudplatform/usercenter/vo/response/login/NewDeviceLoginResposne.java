package com.upuphone.cloudplatform.usercenter.vo.response.login;

import com.upuphone.cloudplatform.usercenter.vo.SimpleUserInfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Classname NewDeviceLoginResposne
 * @Description TODO
 * @Date 2022/3/31 5:38 下午
 * @Created by gz-d
 */
@Getter
@Setter
@ApiModel(value = "新设备二次校验登录返response")
public class NewDeviceLoginResposne {
    @ApiModelProperty(value = "accessToken", required = true)
    private String accessToken;

    @ApiModelProperty(value = "refreshToken", required = true)
    private String refreshToken;

    @ApiModelProperty(value = "用户基本信息")
    private SimpleUserInfoVo simpleUserInfoVo;

    public NewDeviceLoginResposne(String accessToken, String refreshToken, SimpleUserInfoVo simpleUserInfoVo) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.simpleUserInfoVo = simpleUserInfoVo;
    }

    public NewDeviceLoginResposne() {
    }
}
