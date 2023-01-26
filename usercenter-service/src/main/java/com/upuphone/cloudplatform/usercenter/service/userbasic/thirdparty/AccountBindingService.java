package com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Strings;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.constants.ThirdPartyAccountEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.WeChatAccountUtil;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.model.UserThirdAccountBaseInfo;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.model.WeChatTokenBo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.repo.AccountBindingRepo;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.AccountBindingRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.AccountBindingResponse;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class AccountBindingService extends BaseService<AccountBindingRequest, AccountBindingResponse> {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Autowired
    WeChatAccountUtil weChatAccountUtil;

    @Autowired
    private AccountBindingRepo accountBindingRepo;


    @Override
    protected void validate(AccountBindingRequest accountBindingRequest) {
        if (RequestContext.getUserId() == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "未查询到登录态");
        }
    }

    @Override
    protected AccountBindingResponse processCore(AccountBindingRequest accountBindingRequest) throws Exception {
        WeChatTokenBo tokenRep = weChatAccountUtil.getAccessTokenAndOpenIdByCode(accountBindingRequest.getCode());
        if (tokenRep == null) {
            throw new BusinessException(UserCenterErrorCode.BINDING_FAIL, "微信用户信息获取失败");
        }
        UserThirdAccountBaseInfo thridprtUserInfoRep = weChatAccountUtil
                .getUserInfo(tokenRep.getAccessToken(), tokenRep.getOpenId());

        if (thridprtUserInfoRep == null) {
            throw new BusinessException(UserCenterErrorCode.BINDING_FAIL, "微信用户信息获取失败");
        }

        ThirdPartyAccountEnum bingdingType = ThirdPartyAccountEnum.getByType(accountBindingRequest.getBingdingType());
        try {
            accountBindingRepo.addBindingRelationDB(bingdingType, thridprtUserInfoRep.getUid());
        } catch (Exception e) {
            log.warn(Strings.lenientFormat("binding error userid: %s, unionId: %s",
                    RequestContext.getUserId(), thridprtUserInfoRep.getUid()), e);
            throw e;
        }

        this.updateUserInfoToDb(thridprtUserInfoRep);

        return new AccountBindingResponse();
    }

    private void updateUserInfoToDb(UserThirdAccountBaseInfo userThirdAccountBaseInfo) {
        UserBaseInfoPo userBaseInfoPo = userBaseInfoMapper.selectOne(Wrappers.<UserBaseInfoPo>lambdaQuery()
                .eq(UserBaseInfoPo::getId, RequestContext.getUserId()));

        if (userBaseInfoPo == null) {
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, "用户信息不存在");
        }
        if (Strings.isNullOrEmpty(userBaseInfoPo.getUserName())) {
            userBaseInfoPo.setUserName(userThirdAccountBaseInfo.getNickname());
        }
        if (Strings.isNullOrEmpty(userBaseInfoPo.getPhotoUrl()) && !StringUtil.isEmpty(userThirdAccountBaseInfo.getHeadImgUrl())) {
            if (!StringUtils.isEmpty(userThirdAccountBaseInfo.getHeadImgUrl())) {
                String newPhotoUrl = weChatAccountUtil.uploadWechatPhoto(userThirdAccountBaseInfo.getHeadImgUrl());
                userBaseInfoPo.setPhotoUrl(newPhotoUrl);
            }
        }
        if (userBaseInfoPo.getGender().equals(0)) {
            userBaseInfoPo.setGender(userThirdAccountBaseInfo.getSex());
        }
        userBaseInfoMapper.updateById(userBaseInfoPo);
    }
}
