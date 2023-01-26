package com.upuphone.cloudplatform.usercenter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleUserInfoVo {
    @ApiModelProperty(value = "userId")
    private Long userId;

    @ApiModelProperty(value = "昵称")
    private String userName;

    @ApiModelProperty(value = "头像")
    private String photoUrl;

    public SimpleUserInfoVo() {
    }

    public SimpleUserInfoVo(Long userId, String userName, String photoUrl) {
        this.userId = userId;
        this.userName = userName;
        this.photoUrl = photoUrl;
    }
}
