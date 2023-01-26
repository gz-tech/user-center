package com.upuphone.cloudplatform.usercenter.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("用户信息")
public class UserInfoVo {

    @ApiModelProperty(value = "userId")
    private Long userId;

    @ApiModelProperty(value = "boxingId")
    private String boxingId;

    @ApiModelProperty(value = "昵称")
    private String userName;

    @ApiModelProperty(value = "头像")
    private String photoUrl;

    @ApiModelProperty(value = "电话区号")
    private String phoneCode;

    @ApiModelProperty(value = "电话号码")
    private String phoneNumberMask;

    @ApiModelProperty(value = "安全手机号区号")
    private String securityPhoneCode;

    @ApiModelProperty(value = "安全手机号")
    private String securityPhoneNumberMask;

    @ApiModelProperty(value = "邮箱地址")
    private String emailMask;

    @ApiModelProperty(value = "性别")
    private Integer gender;//0 未设置 1 male 2 female 3 保密

    @ApiModelProperty(value = "国家code")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    private String countryName;

    public UserInfoVo() {
    }

    public UserInfoVo(Long userId, String userName, String photoUrl) {
        this.userId = userId;
        this.userName = userName;
        this.photoUrl = photoUrl;
    }
}
