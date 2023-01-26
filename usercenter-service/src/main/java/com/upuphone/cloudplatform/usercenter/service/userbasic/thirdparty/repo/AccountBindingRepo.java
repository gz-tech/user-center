package com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.repo;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.common.util.encryption.AESUtil;
import com.upuphone.cloudplatform.usercenter.constants.ThirdPartyAccountEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserThirdPartyAccountPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserThirdPartyAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AccountBindingRepo {

    @Autowired
    private UserThirdPartyAccountMapper userThirdPartyAccountMapper;


    @Transactional(timeout = 1000)
    public void addBindingRelationDB(ThirdPartyAccountEnum bindingType, String uniqueId) {
        bindedCheck(RequestContext.getUserId(), uniqueId, bindingType);
        insertThirdPartUser(RequestContext.getUserId(), uniqueId, bindingType);
    }

    public void bindedCheck(Long userId, String uid, ThirdPartyAccountEnum bindingType) {
        UserThirdPartyAccountPo existXingji = userThirdPartyAccountMapper
                .selectOne(Wrappers.<UserThirdPartyAccountPo>lambdaQuery()
                        .eq(UserThirdPartyAccountPo::getUserId, userId)
                        .eq(UserThirdPartyAccountPo::getType, bindingType.getType()));
        UserThirdPartyAccountPo existWechat = userThirdPartyAccountMapper
                .selectOne(Wrappers.<UserThirdPartyAccountPo>lambdaQuery()
                        .eq(UserThirdPartyAccountPo::getUniqueId, uid)
                        .eq(UserThirdPartyAccountPo::getType, bindingType.getType()));
        if (existWechat != null) {
            throw new BusinessException(UserCenterErrorCode.THIRD_PART_ALREADY_BINDED);
        }
        if (existXingji != null) {
            throw new BusinessException(UserCenterErrorCode.XIJING_ALREADY_BINDED);
        }
    }

    public void insertThirdPartUser(Long userId, String uid, ThirdPartyAccountEnum bindingType) {
        UserThirdPartyAccountPo insertPo = new UserThirdPartyAccountPo();
        insertPo.setType(bindingType.getType());
        insertPo.setUniqueId(uid);
        insertPo.setUserId(userId);
        userThirdPartyAccountMapper.insert(insertPo);
    }
    public void checkIfBindedBeforeSendValidCode(String phoneNumber,  Integer thirdBindType) {
        UserThirdPartyAccountPo existXingji = userThirdPartyAccountMapper.selectByPhoneNumberAndType(phoneNumber, thirdBindType);
        if (existXingji != null) {
            throw new BusinessException(UserCenterErrorCode.XIJING_ALREADY_BINDED);
        }
    }
}
