package com.upuphone.cloudplatform.usercenter.vo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("用户信息")
public class UserInfoVo {
    private Long userId;
    private String boxingId;
    private String userName;
    private String phoneNumber;
    private String phoneCode;
    private String securityPhoneNumber;
    private String photoUrl;
    private String email;
    private Integer gender;//0 未设置 1 male 2 female 3 保密
    private String countryCode;
    private String countryName;
}
