package com.upuphone.cloudplatform.usercenter.service.userbasic.converter;

import com.upuphone.cloudplatform.common.model.mobile.UserInfo;
import com.upuphone.cloudplatform.usercenter.common.util.MaskUtil;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.vo.UserInfoVo;

public class UserInfoConverter {

    public static UserInfoVo convertFrom(UserBaseInfoPo userBaseInfoPo) {
        if (userBaseInfoPo == null) {
            return null;
        }
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setBoxingId(userBaseInfoPo.getBoxingId());
        userInfoVo.setUserName(userBaseInfoPo.getUserName());
        userInfoVo.setPhoneNumber(userBaseInfoPo.getPhoneNumber());
        userInfoVo.setSecurityPhoneNumber(MaskUtil.maskPhone(userBaseInfoPo.getSecurityPhoneNumber()));
        userInfoVo.setGender(userBaseInfoPo.getGender());
        userInfoVo.setCountryCode(userBaseInfoPo.getCountryCode());
        userInfoVo.setCountryName(userBaseInfoPo.getCountryName());
        userInfoVo.setPhotoUrl(userBaseInfoPo.getPhotoUrl());
        userInfoVo.setEmail(MaskUtil.maskEmail(userBaseInfoPo.getEmail()));
        return userInfoVo;
    }

    public static UserInfo userBaseInfoPo2UserInfo(UserBaseInfoPo userBaseInfoPo) {
        if (userBaseInfoPo == null) {
            return null;
        } else {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userBaseInfoPo.getId());
            userInfo.setUserName(userBaseInfoPo.getUserName());
            userInfo.setPhoneNumber(userBaseInfoPo.getPhoneNumber());
            userInfo.setGender(userBaseInfoPo.getGender());
            userInfo.setCountryCode(userBaseInfoPo.getCountryCode());
            userInfo.setCountryName(userBaseInfoPo.getCountryName());
            userInfo.setBoxingId(userBaseInfoPo.getBoxingId());
            userInfo.setPhotoUrl(userBaseInfoPo.getPhotoUrl());
            return userInfo;
        }
    }
}
