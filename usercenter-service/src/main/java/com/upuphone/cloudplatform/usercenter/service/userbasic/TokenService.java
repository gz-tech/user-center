package com.upuphone.cloudplatform.usercenter.service.userbasic;

import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.entity.DeviceInfo;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthClientDetailPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthRefreshTokenPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthClientDetailMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthRefreshTokenMapper;
import com.upuphone.cloudplatform.usercenter.service.util.AccessTokenUtil;
import com.upuphone.cloudplatform.usercenter.service.util.RefreshTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/7
 */
@Service
@Slf4j
public class TokenService {

    @Autowired
    private OauthRefreshTokenMapper oauthRefreshTokenMapper;
    @Autowired
    private OauthClientDetailMapper oauthClientDetailMapper;
    @Autowired
    private RefreshTokenUtil refreshTokenUtil;
    @Autowired
    private AccessTokenUtil accessTokenUtil;

    @Transactional(rollbackFor = Exception.class, timeout = 20)
    public List<String> tokenPersistence(Long userId,
            String appId,
            OauthClientDetailPo clientDetailPo, DeviceInfo oauthDeviceInfo) {
        Integer rfTokenValidPeriodInSecond = clientDetailPo.getRefreshTokenValidity();
        log.info("生成refreshToken");
        DeviceInfo normalDeviceInfo = new DeviceInfo(RequestContext.getModel(), RequestContext.getDeviceId(),
                RequestContext.getDeviceType(), RequestContext.getDeviceName(), appId);
        DeviceInfo deviceInfo = oauthDeviceInfo == null ? normalDeviceInfo : oauthDeviceInfo;
        String refreshToken = refreshTokenUtil.generateRefreshToken(userId, rfTokenValidPeriodInSecond, deviceInfo);
        OauthRefreshTokenPo oauthRefreshTokenPo = new OauthRefreshTokenPo(userId, appId,
                RequestContext.getDeviceId(), RequestContext.getModel(),
                LocalDateTime.now().plusSeconds(rfTokenValidPeriodInSecond), refreshToken,
                RequestContext.getDeviceName(), RequestContext.getDeviceType());
        int count = oauthRefreshTokenMapper.insert(oauthRefreshTokenPo);
        if (count != 1) {
            log.error("userId: {}, deviceId: {} refreshToken插入失败", userId, RequestContext.getDeviceId());
            throw new BusinessException(CommonErrorCode.SERVICE_ERROR);
        }

        Integer accessTokenValidPeriod = clientDetailPo.getAccessTokenValidity();
        String accessToken = accessTokenUtil.generateAccessToken(userId, accessTokenValidPeriod,
                oauthRefreshTokenPo.getId(), deviceInfo);

        return Arrays.asList(accessToken, refreshToken);
    }

    @Transactional(rollbackFor = Exception.class, timeout = 20)
    public List<String> tokenPersistence(Long userId) {
        OauthClientDetailPo clientDetailPo = oauthClientDetailMapper.getByClientId(RequestContext.getAppId());
        if (clientDetailPo == null) {
            throw new BusinessException(UserCenterErrorCode.APP_NOT_REGISTER);
        }
        return this.tokenPersistence(userId, RequestContext.getAppId(), clientDetailPo, null);
    }

    public String generateAccessToken(Long userId, String appId,
            Long refreshTokenId, OauthClientDetailPo clientDetailPo) {
        Integer accessTokenValidPeriod = clientDetailPo.getAccessTokenValidity();
        DeviceInfo deviceInfo = new DeviceInfo(RequestContext.getModel(), RequestContext.getDeviceId(),
                RequestContext.getDeviceType(), RequestContext.getDeviceName(), appId);
        return accessTokenUtil.generateAccessToken(userId, accessTokenValidPeriod, refreshTokenId, deviceInfo);
    }

    public String generateAccessToken(Long userId, Long refreshTokenId) {
        OauthClientDetailPo clientDetailPo = oauthClientDetailMapper.getByClientId(RequestContext.getAppId());
        if (clientDetailPo == null) {
            throw new BusinessException(UserCenterErrorCode.APP_NOT_REGISTER);
        }
        return this.generateAccessToken(userId, RequestContext.getAppId(), refreshTokenId, clientDetailPo);
    }
}
