package com.upuphone.cloudplatform.usercenter.service.userbasic;

import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.common.util.MaskUtil;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.vo.UserInfoVo;
import com.upuphone.cloudplatform.usercenter.vo.response.GetUserDetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDetailService {

    @Autowired
    private CommonService commonService;

    protected void validate() {
        if (null == RequestContext.getUserId()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "userId can not be null");
        }
    }

    public GetUserDetailResponse process() {
        UserBaseInfoPo userBaseInfoPo = commonService.getUserBaseInfoById(RequestContext.getUserId());
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setBoxingId(userBaseInfoPo.getBoxingId());
        userInfoVo.setUserName(userBaseInfoPo.getUserName());
        userInfoVo.setPhoneNumber(userBaseInfoPo.getPhoneNumber());
        userInfoVo.setSecurityPhoneNumber(MaskUtil.maskPhone(userBaseInfoPo.getSecurityPhoneNumber()));
        userInfoVo.setGender(userBaseInfoPo.getGender());
        userInfoVo.setCountryCode(userBaseInfoPo.getCountryCode());
        userInfoVo.setCountryName(userBaseInfoPo.getCountryName());
        userInfoVo.setPhotoUrl(userBaseInfoPo.getPhotoUrl());
        userInfoVo.setPhoneCode(userBaseInfoPo.getPhoneCode());
        userInfoVo.setEmail(MaskUtil.maskEmail(userBaseInfoPo.getEmail()));
        GetUserDetailResponse result = new GetUserDetailResponse();
        result.setUserInfoVo(userInfoVo);
        return result;
    }
}
