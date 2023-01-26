package com.upuphone.cloudplatform.usercenter.service.authentication;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.auth.vo.AuthErrorCode;
import com.upuphone.cloudplatform.usercenter.auth.vo.generatetoken.GenerateTokenRequest;
import com.upuphone.cloudplatform.usercenter.auth.vo.generatetoken.GenerateTokenResponse;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.entity.DeviceInfo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthClientDetailPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthRefreshTokenPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthClientDetailMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthRefreshTokenMapper;
import com.upuphone.cloudplatform.usercenter.service.authentication.model.AuthCodeBo;
import com.upuphone.cloudplatform.usercenter.service.authentication.util.AuthCodeUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.TokenService;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/29
 */
@Service
@Slf4j
public class GenerateTokenService extends BaseService<GenerateTokenRequest, GenerateTokenResponse> {

    @Autowired
    private AuthCodeUtil authCodeUtil;
    @Autowired
    private OauthRefreshTokenMapper refreshTokenMapper;
    @Autowired
    private OauthClientDetailMapper clientDetailMapper;
    @Autowired
    private Setting setting;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private TokenService tokenService;

    @Override
    protected void validate(GenerateTokenRequest request) {
        if (!StringUtils.equalsIgnoreCase("authorization_code", request.getGrantType())) {
            throw new BusinessException(AuthErrorCode.INVALID_GRANT_TYPE);
        }
        if (!setting.getOauthAppIds().contains(request.getAppId())) {
            throw new BusinessException(AuthErrorCode.APP_OAUTH_FORBIDDEN);
        }
    }

    @Override
    protected GenerateTokenResponse processCore(GenerateTokenRequest request) throws Exception {
        String appId = request.getAppId();
        AuthCodeBo bo = authCodeUtil.getCodeInfo(request.getCode(), "user");
        if (!Objects.equals(bo.getCode(), request.getCode())) {
            throw new BusinessException(AuthErrorCode.CODE_EXPIRED);
        }
        String secret = request.getSecret();
        OauthClientDetailPo clientDetailPo = clientDetailMapper.selectOne(Wrappers.<OauthClientDetailPo>lambdaQuery()
                .eq(OauthClientDetailPo::getAppId, appId)
                .eq(OauthClientDetailPo::getClientSecret, secret));
        if (null == clientDetailPo) {
            throw new BusinessException(AuthErrorCode.APP_NOT_REGISTERED);
        }
        String lockKey = RedisKeys.authTokenLockKey(bo.getUserId().toString(), appId, bo.getDeviceId());
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(0, 10, TimeUnit.SECONDS)) {
                throw new BusinessException(CommonErrorCode.OPERATION_ERROR, "please not repeat request");
            }
            // 查询是否有生效的refreshToken
            OauthRefreshTokenPo refreshTokenPo = refreshTokenMapper.selectOne(Wrappers.<OauthRefreshTokenPo>lambdaQuery()
                    .eq(OauthRefreshTokenPo::getAppId, appId)
                    .eq(OauthRefreshTokenPo::getUserId, bo.getUserId())
                    .eq(OauthRefreshTokenPo::getDeviceId, bo.getDeviceId())
                    .ge(OauthRefreshTokenPo::getExpireTime, LocalDateTime.now())
                    .orderByDesc(OauthRefreshTokenPo::getId)
                    .last(" limit 1"));
            Integer expiresIn = clientDetailPo.getAccessTokenValidity();
            GenerateTokenResponse response = new GenerateTokenResponse().setExpiresIn(expiresIn).setScope(bo.getScope());
            // todo token生成需要考虑scope
            if (null != refreshTokenPo) {
                // 存在生效refreshToken记录，直接生成对应的accessToken
                authCodeUtil.delCode(request.getCode(), "user");
                String accessToken = tokenService.generateAccessToken(bo.getUserId(), appId, refreshTokenPo.getId(), clientDetailPo);
                return response.setAccessToken(accessToken).setRefreshToken(refreshTokenPo.getRefreshToken());
            }
            // 如果没有生效refreshToken记录，重新生成一对token
            List<String> tokens = tokenService.tokenPersistence(bo.getUserId(), appId, clientDetailPo, buildDeviceInfo(bo));
            authCodeUtil.delCode(request.getCode(), "user");
            return response.setAccessToken(tokens.get(0)).setRefreshToken(tokens.get(1));
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private DeviceInfo buildDeviceInfo(AuthCodeBo bo) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId(bo.getDeviceId());
        deviceInfo.setDeviceName(bo.getDeviceName());
        deviceInfo.setDeviceType(bo.getDeviceType());
        deviceInfo.setModel(bo.getModel());
        deviceInfo.setAppId(bo.getAppId());
        return deviceInfo;
    }
}
