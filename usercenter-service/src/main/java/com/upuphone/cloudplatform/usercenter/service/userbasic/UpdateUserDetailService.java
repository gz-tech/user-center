package com.upuphone.cloudplatform.usercenter.service.userbasic;

import com.google.common.base.Strings;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.repo.UserInfoRepo;
import com.upuphone.cloudplatform.usercenter.service.basic.util.CheckTextUtil;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.converter.UserInfoConverter;
import com.upuphone.cloudplatform.usercenter.vo.UserInfoVo;
import com.upuphone.cloudplatform.usercenter.vo.request.UpdateUserInfoRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.CheckTextRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.UpdateUserInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class UpdateUserDetailService extends BaseService<UpdateUserInfoRequest, UpdateUserInfoResponse> {

    private static final String BOXING_ID_REGX = "^[a-zA-Z0-9#.+_-]+$";

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Autowired
    private CheckTextUtil checkTextUtil;

    @Autowired
    private CommonService commonService;

    @Override
    protected void validate(UpdateUserInfoRequest getUserDetailRequest) {
        if (null == RequestContext.getUserId()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "user Id不能为空");
        }

        if (!Strings.isNullOrEmpty(getUserDetailRequest.getBoxingId())) {
            Pattern p = Pattern.compile(BOXING_ID_REGX);
            Matcher m = p.matcher(getUserDetailRequest.getBoxingId()); // 获取 matcher 对象
            if (!m.matches()) {
                throw new BusinessException(CommonErrorCode.PARAM_ERROR, "boxing id must only contain a-zA-Z0-9#.+_-");
            }
        }

        if (!Strings.isNullOrEmpty(getUserDetailRequest.getUserName())) {
            // username 敏感词检测
            CheckTextRequest checkTextRequest  = new CheckTextRequest();
            checkTextRequest.setDataId(RequestContext.getUserId() + "");
            checkTextRequest.setContent(getUserDetailRequest.getUserName());
            checkTextUtil.textValidation(checkTextRequest);
            getUserDetailRequest.setUserName(getUserDetailRequest.getUserName().trim());
        }
    }

    @Override
    protected UpdateUserInfoResponse processCore(UpdateUserInfoRequest getUserDetailRequest) throws Exception {
        UserBaseInfoPo userBaseInfoPo = commonService.getUserBaseInfoById(RequestContext.getUserId());
        if (userBaseInfoPo == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "user null");
        }

        if (!Strings.isNullOrEmpty(getUserDetailRequest.getBoxingId())) {
            // BoxingId 敏感词检测
            CheckTextRequest checkTextRequest  = new CheckTextRequest();
            checkTextRequest.setDataId(RequestContext.getUserId() + "");
            checkTextRequest.setContent(getUserDetailRequest.getBoxingId());
            checkTextUtil.textValidation(checkTextRequest);
            userInfoRepo.setUserBoxingId(userBaseInfoPo.getId(), getUserDetailRequest.getBoxingId());
        }

        this.updateUserInfo(getUserDetailRequest, userBaseInfoPo.getId(), userBaseInfoPo.getUserName());
        userBaseInfoPo = commonService.getUserBaseInfoById(RequestContext.getUserId());
        UserInfoVo userInfoVo = UserInfoConverter.convertFrom(userBaseInfoPo);
        UpdateUserInfoResponse result = new UpdateUserInfoResponse();
        result.setUserInfoVo(userInfoVo);
        return result;
    }

    private void updateUserInfo(UpdateUserInfoRequest getUserDetailRequest, Long userId, String originUserName) {
        UserBaseInfoPo userBaseInfoUpdater = new UserBaseInfoPo();
        userBaseInfoUpdater.setId(userId);
        // username 敏感词检测
        CheckTextRequest checkTextRequest  = new CheckTextRequest();
        checkTextRequest.setDataId(userId + "");
        checkTextRequest.setContent(getUserDetailRequest.getUserName());
        checkTextUtil.textValidation(checkTextRequest);
        userBaseInfoUpdater.setUserName(getUserDetailRequest.getUserName() == null ?
                originUserName : getUserDetailRequest.getUserName());
        userBaseInfoUpdater.setGender(getUserDetailRequest.getGender() == null ? null : getUserDetailRequest.getGender());
        userBaseInfoUpdater.setCountryCode(getUserDetailRequest.getCountryCode() == null ?
                null : getUserDetailRequest.getCountryCode());
        userBaseInfoUpdater.setCountryName(getUserDetailRequest.getCountryName() == null ?
                null : getUserDetailRequest.getCountryName());
        userBaseInfoUpdater.setPhotoUrl(getUserDetailRequest.getPhotoUrl() == null ?
                null : getUserDetailRequest.getPhotoUrl());

        userBaseInfoUpdater.updateById();
    }
}
