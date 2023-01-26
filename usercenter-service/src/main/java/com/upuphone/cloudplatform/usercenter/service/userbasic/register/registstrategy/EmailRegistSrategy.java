package com.upuphone.cloudplatform.usercenter.service.userbasic.register.registstrategy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.utils.ListUtil;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.model.RegistReqBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("emailRegistSrategy")
public class EmailRegistSrategy implements RegistStrategy {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Override
    public String getUniqueKey(RegistReqBo registRequest) {
        return registRequest.getEmailAddress();
    }

    @Override
    public void checkKeyNumberUnique(RegistReqBo registRequest) {
        List<UserBaseInfoPo> userBaseInfoPos = userBaseInfoMapper.selectList(Wrappers.<UserBaseInfoPo>lambdaQuery()
                .eq(UserBaseInfoPo::getEmail, registRequest.getEmailAddress()));

        if (ListUtil.any(userBaseInfoPos)) {
            throw new BusinessException(UserCenterErrorCode.ALREADY_REGISTERED, "email already regist");
        }
    }
}
