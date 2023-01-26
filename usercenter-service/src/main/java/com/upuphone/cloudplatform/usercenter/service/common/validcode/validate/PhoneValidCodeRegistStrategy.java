package com.upuphone.cloudplatform.usercenter.service.common.validcode.validate;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.SendValidCodeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PhoneValidCodeRegistStrategy implements ValidCodeValidateStrategy<SendValidCodeRequest> {
    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Override
    public void validate(SendValidCodeRequest request) {
        UserBaseInfoPo existUser = userBaseInfoMapper.selectOne(Wrappers.<UserBaseInfoPo>lambdaQuery()
                .eq(UserBaseInfoPo::getPhoneNumber, request.getPhoneNumber()));
        if (null != existUser) {
            throw new BusinessException(UserCenterErrorCode.ALREADY_REGISTERED);
        }
    }
}
