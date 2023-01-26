package com.upuphone.cloudplatform.usercenter.repo;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthRefreshTokenPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthRefreshTokenMapper;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.util.UserSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class OauthRefreshTokenRepo {

    @Autowired
    private OauthRefreshTokenMapper refreshTokenMapper;
    @Autowired
    private UserSecurityUtils userSecurityUtils;

    @Transactional(rollbackFor = Exception.class, timeout = 10)
    public void removeDevice(String deviceId, Long refreshTokenId, Long userId) {
        int cnt = refreshTokenMapper.delete(Wrappers.<OauthRefreshTokenPo>lambdaQuery()
                .eq(OauthRefreshTokenPo::getId, refreshTokenId)
                .eq(OauthRefreshTokenPo::getDeviceId, deviceId)
                .eq(OauthRefreshTokenPo::getUserId, userId));
        if (0 == cnt) {
            log.error("[RemoveDeviceService]移除设备失败, deviceId={}, userId={}", deviceId, userId);
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, "移除设备失败");
        }
        // add logout key
        userSecurityUtils.addKickedKey(refreshTokenId);
    }
}
