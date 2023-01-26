package com.upuphone.cloudplatform.usercenter.service.userbasic.login.util;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname NewDeviceLoginUtil
 * @Description
 * @Date 2022/3/31 11:12 下午
 * @Created by gz-d
 */
@Service
@Slf4j
public class NewDeviceLoginUtil {
    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    public UserBaseInfoPo getUser(Long userId) {
        UserBaseInfoPo user = userBaseInfoMapper.selectOne(Wrappers.<UserBaseInfoPo>lambdaQuery()
                .eq(UserBaseInfoPo::getId, userId)
                .last("for update"));
        if (user != null) {
            return user;
        } else {
            throw new BusinessException(UserCenterErrorCode.USER_NOT_FOUND);
        }
    }

    public Long getUserIdFromSecret(String secret) {
        return Long.parseLong(secret.split("\\+")[1]);
    }
}
