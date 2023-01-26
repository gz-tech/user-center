package com.upuphone.cloudplatform.usercenter.service.common.validcode.validate;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.EmailSendValidCodeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailValidCodeRegistStrategy implements ValidCodeValidateStrategy<EmailSendValidCodeRequest> {
    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Override
    public void validate(EmailSendValidCodeRequest request) {
        UserBaseInfoPo existUser = userBaseInfoMapper.selectOne(Wrappers.<UserBaseInfoPo>lambdaQuery()
                .eq(UserBaseInfoPo::getEmail, request.getEmailAddress()));
        if (null != existUser) {
            throw new BusinessException(UserCenterErrorCode.ALREADY_REGISTERED);
        }
    }
}
