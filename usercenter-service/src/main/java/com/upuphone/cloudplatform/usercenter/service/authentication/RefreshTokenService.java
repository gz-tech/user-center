package com.upuphone.cloudplatform.usercenter.service.authentication;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.auth.vo.refresh.RefreshTokenRequest;
import com.upuphone.cloudplatform.usercenter.auth.vo.refresh.RefreshTokenResponse;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.common.util.DateUtil;
import com.upuphone.cloudplatform.usercenter.common.util.encryption.AESUtil;
import com.upuphone.cloudplatform.usercenter.entity.DeviceInfo;
import com.upuphone.cloudplatform.usercenter.entity.token.TokenBo;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthClientDetailPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthRefreshTokenPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthClientDetailMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthRefreshTokenMapper;
import com.upuphone.cloudplatform.usercenter.service.authentication.model.RefreshProcessContext;
import com.upuphone.cloudplatform.usercenter.service.authentication.util.LastRefreshTokenUtil;
import com.upuphone.cloudplatform.usercenter.service.util.AccessTokenUtil;
import com.upuphone.cloudplatform.usercenter.service.util.RefreshTokenUtil;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/14
 */
@Service
@Slf4j
public class RefreshTokenService extends BaseService<RefreshTokenRequest, RefreshTokenResponse> {

    @Autowired
    private RefreshTokenUtil refreshTokenUtil;
    @Autowired
    private AccessTokenUtil accessTokenUtil;
    @Autowired
    private OauthRefreshTokenMapper oauthRefreshTokenMapper;
    @Autowired
    private OauthClientDetailMapper oauthClientDetailMapper;
    @Autowired
    private LastRefreshTokenUtil lastRefreshTokenUtil;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private Setting setting;

    @Override
    protected void validate(RefreshTokenRequest request) {
        if (StringUtils.isBlank(RequestContext.getAppId())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "appid????????????");
        }
        if (StringUtils.isBlank(RequestContext.getModel())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "model????????????");
        }
    }

    @Override
    protected RefreshTokenResponse processCore(RefreshTokenRequest soaRequest) throws Exception {
        String lockKey = RedisKeys.refreshTokenLockKey(soaRequest.getRefreshToken());
        RLock lock = redissonClient.getLock(lockKey);
        RefreshTokenResponse result = new RefreshTokenResponse();
        try {
            if (!lock.tryLock(0, 20, TimeUnit.SECONDS)) {
                throw new BusinessException(CommonErrorCode.OPERATION_ERROR, "please not repeat request");
            }
            RefreshProcessContext ctx = this.buildProcessContext(soaRequest);
            boolean isValid = this.checkRefreshTokenValidation(ctx.getRequestTokenBO());
            if (!isValid) {
                this.deleteInvalidTokenInDB(ctx.getRequestTokenBO());
                throw new BusinessException(UserCenterErrorCode.REFRESH_TOKEN_EXPIRED);
            }
            if (ctx.getValidTokenPo().getRefreshToken().equals(ctx.getRequestTokenBO().getTokenStr())) {
                // ?????????????????????refreshToken ????????????????????????????????????refreshToken
                this.doRefresh(ctx, result);
                result.setAccessTokenExpiresAt(DateUtil.asDate(
                        accessTokenUtil.parseToken(result.getAccessToken()).getExpireTime()).getTime());
                result.setRefreshTokenExpiresAt(DateUtil.asDate(
                        refreshTokenUtil.parseToken(AESUtil.decrypt(result.getRefreshToken(),
                                setting.getRefreshTokenAesKey(),
                                setting.getRefreshTokenAesIV()),
                                setting.getRefreshTokenSign()).getExpireTime()).getTime());
                return result;
            }
            if (ctx.isLastReqRefreshToken()) {
                // ??????????????????????????????????????????refreshToken?????????????????????refresh?????????refreshToken
                this.returnLastGeneratedToken(ctx, result);
            } else {
                throw new BusinessException(UserCenterErrorCode.INVALID_TOKEN);
            }
            result.setAccessTokenExpiresAt(DateUtil.asDate(
                    accessTokenUtil.parseToken(result.getAccessToken()).getExpireTime()).getTime());
            result.setRefreshTokenExpiresAt(DateUtil.asDate(
                    refreshTokenUtil.parseToken(AESUtil.decrypt(result.getRefreshToken(),
                            setting.getRefreshTokenAesKey(),
                            setting.getRefreshTokenAesIV()),
                            setting.getRefreshTokenSign()).getExpireTime()).getTime());
            return result;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * ?????????????????????accessToken???refreshToken
     *
     * @param ctx    ?????????
     * @param result response
     */
    private void returnLastGeneratedToken(RefreshProcessContext ctx, RefreshTokenResponse result) {
        result.setRefreshToken(ctx.getValidTokenPo().getRefreshToken());
        String lastNewAccessTokenInRedis = lastRefreshTokenUtil.getNewAccessTokenInRedis(ctx.getDeviceInfo(),
                ctx.getUserId());
        if (lastNewAccessTokenInRedis == null) {
            LocalDateTime issueAt = LocalDateTime.now();
            lastNewAccessTokenInRedis = accessTokenUtil.generateAccessToken(ctx.getUserId(), issueAt,
                    ctx.getClientDetailPo().getAccessTokenValidity(),
                    ctx.getValidTokenPo().getId(), ctx.getDeviceInfo());
            LocalDateTime expireDate = issueAt.plusSeconds(ctx.getClientDetailPo().getAccessTokenValidity());
            lastRefreshTokenUtil.setNewAccessTokenInRedis(ctx.getDeviceInfo(), ctx.getUserId(),
                    lastNewAccessTokenInRedis, expireDate);
        }
        result.setAccessToken(lastNewAccessTokenInRedis);
    }

    /**
     * ??????????????????????????????refreshToken???????????????accessToken????????????redis??????????????????????????????refreshToken?????????response
     *
     * @param ctx    ???????????????
     * @param result response
     */
    private void doRefresh(RefreshProcessContext ctx, RefreshTokenResponse result) {
        // new refresh token to make sure the refresh token can only be used once
        String newRefreshToken = refreshTokenUtil.generateRefreshToken(ctx.getUserId(),
                ctx.getValidTokenPo().getExpireTime(),
                ctx.getDeviceInfo());
        this.updateTokenInDB(ctx.getValidTokenPo(), newRefreshToken);
        result.setRefreshToken(newRefreshToken);
        LocalDateTime issueAt = LocalDateTime.now();
        Integer accessTokenValidTime = ctx.getClientDetailPo().getAccessTokenValidity();
        String accessToken = accessTokenUtil.generateAccessToken(ctx.getUserId(), issueAt, accessTokenValidTime,
                ctx.getValidTokenPo().getId(), ctx.getDeviceInfo());
        result.setAccessToken(accessToken);
        LocalDateTime expireDate = issueAt.plusSeconds(ctx.getClientDetailPo().getAccessTokenValidity());
        //??????????????????????????????refreshToken ???????????????refreshToken?????????
        lastRefreshTokenUtil.updateLastRequestRefreshTokenInRedis(ctx.getRequestTokenBO(), expireDate);
        lastRefreshTokenUtil.setNewAccessTokenInRedis(ctx.getDeviceInfo(), ctx.getUserId(), accessToken, expireDate);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param soaRequest ????????????
     * @return RefreshProcessContext
     */
    private RefreshProcessContext buildProcessContext(RefreshTokenRequest soaRequest) {
        RefreshProcessContext refreshProcessContext = new RefreshProcessContext();
        TokenBo requestTokenBO = refreshTokenUtil.decryptRefreshToken(soaRequest.getRefreshToken());
        refreshProcessContext.setRequestTokenBO(requestTokenBO);
        if (!RequestContext.getDeviceId().equals(requestTokenBO.getDeviceId())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "???????????????????????????token");
        }
        refreshProcessContext.setUserId(Long.valueOf(requestTokenBO.getUserId()));
        refreshProcessContext.setDeviceInfo(new DeviceInfo(requestTokenBO.getModel(), requestTokenBO.getDeviceId(),
                requestTokenBO.getDeviceType(), requestTokenBO.getDeviceName(), requestTokenBO.getAppid()));
        OauthClientDetailPo clientDetailPo = oauthClientDetailMapper.getByClientId(requestTokenBO.getAppid());
        if (clientDetailPo == null) {
            throw new BusinessException(UserCenterErrorCode.APP_NOT_REGISTER);
        }
        refreshProcessContext.setClientDetailPo(clientDetailPo);
        OauthRefreshTokenPo validTokenPo = oauthRefreshTokenMapper
                .selectOne(Wrappers.<OauthRefreshTokenPo>lambdaQuery()
                        .eq(OauthRefreshTokenPo::getAppId, refreshProcessContext.getDeviceInfo().getAppId())
                        .eq(OauthRefreshTokenPo::getUserId, refreshProcessContext.getUserId())
                        .eq(OauthRefreshTokenPo::getDeviceId, refreshProcessContext.getDeviceInfo().getDeviceId())
                        .eq(OauthRefreshTokenPo::getDeviceType, refreshProcessContext.getDeviceInfo().getDeviceType()));
        if (validTokenPo == null) {
            throw new BusinessException(UserCenterErrorCode.INVALID_TOKEN);
        }
        refreshProcessContext.setValidTokenPo(validTokenPo);
        // ??????????????????????????????refreshToken
        boolean isLastReqRefreshTokenInRedis = lastRefreshTokenUtil.isLastReqRefreshTokenInRedis(requestTokenBO, requestTokenBO.getTokenStr());
        refreshProcessContext.setLastReqRefreshToken(isLastReqRefreshTokenInRedis);
        return refreshProcessContext;
    }

    /**
     * ?????????????????????????????????refreshToken
     *
     * @param originPo        ??????????????????refreshToken entity
     * @param newRefreshToken ???refreshToken ?????????
     */
    private void updateTokenInDB(OauthRefreshTokenPo originPo, String newRefreshToken) {
        OauthRefreshTokenPo refreshTokenPoUpdater = new OauthRefreshTokenPo();
        refreshTokenPoUpdater.setRefreshToken(newRefreshToken);
        boolean refreshTokenUpdateStatus = refreshTokenPoUpdater.update(Wrappers.<OauthRefreshTokenPo>lambdaUpdate()
                .eq(OauthRefreshTokenPo::getId, originPo.getId())
                .eq(OauthRefreshTokenPo::getRefreshToken, originPo.getRefreshToken()));
        if (!refreshTokenUpdateStatus) {
            log.error("tokenId: {}, newTokenStr : {}, originTokenStr:{} refreshToken????????????",
                    originPo.getId(), newRefreshToken, originPo.getRefreshToken());
            throw new BusinessException(CommonErrorCode.SERVICE_ERROR);
        }
    }

    /**
     * ??????????????????????????????refreshToken
     *
     * @param requestTokenBO requestTokenBO
     */
    private void deleteInvalidTokenInDB(TokenBo requestTokenBO) {
        Long userId = Long.valueOf(requestTokenBO.getUserId());
        oauthRefreshTokenMapper.delete(Wrappers.<OauthRefreshTokenPo>lambdaQuery()
                .eq(OauthRefreshTokenPo::getAppId, requestTokenBO.getAppid())
                .eq(OauthRefreshTokenPo::getUserId, userId)
                .eq(OauthRefreshTokenPo::getDeviceId, requestTokenBO.getDeviceId())
                .eq(OauthRefreshTokenPo::getDeviceType, requestTokenBO.getDeviceType())
                .eq(OauthRefreshTokenPo::getRefreshToken, requestTokenBO.getTokenStr()));
    }

    /**
     * ??????tokenBO?????????
     *
     * @param tokenBO tokenBO
     * @return true - valid; false - expired
     */
    private boolean checkRefreshTokenValidation(TokenBo tokenBO) {
        if (!tokenBO.getDeviceId().equals(RequestContext.getDeviceId())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "???????????????????????????token");
        }
        if (Objects.equals(Boolean.TRUE, tokenBO.getExpired())) {
            log.warn("[RefreshTokenService] refreshToken????????????req={}", JsonUtility.toJson(tokenBO));
            return false;
        }
        return true;
    }
}
