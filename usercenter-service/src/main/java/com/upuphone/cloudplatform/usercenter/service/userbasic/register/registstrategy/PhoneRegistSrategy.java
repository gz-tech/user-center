package com.upuphone.cloudplatform.usercenter.service.userbasic.register.registstrategy;

import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.utils.ListUtil;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.model.RegistReqBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("phoneRegistSrategy")
public class PhoneRegistSrategy implements RegistStrategy {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Override
    public String getUniqueKey(RegistReqBo registRequest) {

        return registRequest.getPhoneCode() + registRequest.getPhoneNumber();
    }

    @Override
    public void checkKeyNumberUnique(RegistReqBo registRequest) {

        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("phone_number", registRequest.getPhoneNumber());
        queryParam.put("phone_code", registRequest.getPhoneCode());
        List<UserBaseInfoPo> userBaseInfoPos = userBaseInfoMapper.selectByMap(queryParam);
        if (ListUtil.any(userBaseInfoPos)) {
            throw new BusinessException(UserCenterErrorCode.ALREADY_REGISTERED);
        }
    }
}
