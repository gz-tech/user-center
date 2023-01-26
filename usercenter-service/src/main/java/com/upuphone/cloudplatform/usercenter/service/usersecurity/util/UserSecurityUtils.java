package com.upuphone.cloudplatform.usercenter.service.usersecurity.util;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.common.util.MaskUtil;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.common.util.SessionUtil;
import com.upuphone.cloudplatform.usercenter.common.util.encryption.AESUtil;
import com.upuphone.cloudplatform.usercenter.constants.BoundTypeEnum;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthClientDetailPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthRefreshTokenPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthClientDetailMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthRefreshTokenMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.redis.MaskTypeEnum;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.redis.SessionTypeEnum;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.ValidCodeUtils;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.checker.ValidCodeCheckerTypeEnum;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ForgotPasswordRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ForgotPasswordValidateRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.MobileAndEmailRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.ForgotPasswordValidateResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.MobileAndEmailResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/10
 */
@Service
@Slf4j
public class UserSecurityUtils {

    private static final String LOGOUT = "LOGOUT";
    private static final String KICKED = "KICKED";
    private static final String DELETED = "DELETED";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Setting setting;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ValidCodeUtils validCodeUtils;
    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private OauthRefreshTokenMapper refreshTokenMapper;
    @Autowired
    private OauthClientDetailMapper clientDetailMapper;

    public void batchAddAccountDeleteKey(Set<Long> refreshTokenIds) {
        LocalDateTime now = LocalDateTime.now();
        // 查询对应refreshToken
        List<OauthRefreshTokenPo> refreshTokenPos = refreshTokenMapper.getByRefreshIdsIncludeDeleted(refreshTokenIds).stream()
                .filter(o -> !o.getExpireTime().isBefore(now)).collect(Collectors.toList());
        // refreshTokenId-appId
        Map<Long, String> refreshTokenIdAppIdMap = refreshTokenPos.stream()
                .collect(Collectors.toMap(OauthRefreshTokenPo::getId, OauthRefreshTokenPo::getAppId, (v1, v2) -> v1));
        Set<String> appIds = refreshTokenPos.stream().map(OauthRefreshTokenPo::getAppId).collect(Collectors.toSet());
        List<OauthClientDetailPo> clientDetailPos = clientDetailMapper.selectList(Wrappers.<OauthClientDetailPo>lambdaQuery()
                .in(OauthClientDetailPo::getAppId, appIds));
        // appId-accessToken有效期
        Map<String, Integer> appIdAccessTokenDurationMap = clientDetailPos.stream()
                .collect(Collectors.toMap(OauthClientDetailPo::getAppId, OauthClientDetailPo::getAccessTokenValidity, (v1, v2) -> v1));
        stringRedisTemplate.executePipelined((RedisCallback<String>) connection -> {
            RedisSerializer<String> stringSerializer = stringRedisTemplate.getStringSerializer();
            refreshTokenIdAppIdMap.forEach((refreshTokenId, appId) ->
                    connection.set(stringSerializer.serialize(getLogoutKey(refreshTokenId)), stringSerializer.serialize(DELETED),
                            Expiration.seconds(appIdAccessTokenDurationMap.get(appId)), RedisStringCommands.SetOption.UPSERT));
            return null;
        });
        Set<String> logoutKeys = refreshTokenIdAppIdMap.keySet().stream()
                .map(this::getLogoutKey).filter(Objects::nonNull).collect(Collectors.toSet());
        log.info("[batchAddAccountDeleteKey] logoutKeys={}", JsonUtility.toJson(logoutKeys));
    }

    /**
     * 增加登出缓存状态
     *
     * @param refreshTokenId refreshTokenId
     */
    public void addLogoutKey(Long refreshTokenId) {
        String logoutKey = getLogoutKey(refreshTokenId);
        OauthClientDetailPo clientDetailPo = commonService.getClientDetailByRefreshTokenId(refreshTokenId);
        Integer accessTokenValidity = clientDetailPo.getAccessTokenValidity();
        stringRedisTemplate.opsForValue().set(logoutKey, LOGOUT, accessTokenValidity, TimeUnit.SECONDS);
        log.info("[addLogoutKey] key={}", logoutKey);
    }

    /**
     * 增加被移除设备缓存状态
     *
     * @param refreshTokenId refreshTokenId
     */
    public void addKickedKey(Long refreshTokenId) {
        // add logout keys
        String logoutKey = getLogoutKey(refreshTokenId);
        OauthClientDetailPo clientDetailPo = commonService.getClientDetailByRefreshTokenId(refreshTokenId);
        Integer accessTokenValidity = clientDetailPo.getAccessTokenValidity();
        stringRedisTemplate.opsForValue().set(logoutKey, KICKED, accessTokenValidity, TimeUnit.SECONDS);
        log.info("[addKickedKey] key={}", logoutKey);
    }

    /**
     * 移除手机&邮箱掩码缓存
     *
     * @param po UserBaseInfoPo
     */
    public void removeMaskMobileEmailUserKey(UserBaseInfoPo po) {
        if (StringUtils.isNotBlank(po.getBoxingId())) {
            stringRedisTemplate.delete(RedisKeyUtils.getMaskMobileEmailUserKey(MaskTypeEnum.BOXING_ID, po.getBoxingId()));
        }
        stringRedisTemplate.delete(RedisKeyUtils.getMaskMobileEmailUserKey(MaskTypeEnum.USER_ID, po.getId().toString()));
        if (StringUtils.isNotBlank(po.getPhoneNumber()) && StringUtils.isNotBlank(po.getPhoneCode())) {
            String formatted = PhoneUtil.formatPhoneNumber(po.getPhoneCode(), po.getPhoneNumber());
            stringRedisTemplate.delete(RedisKeyUtils.getMaskMobileEmailUserKey(MaskTypeEnum.MOBILE, formatted));
        }
        if (StringUtils.isNotBlank(po.getEmail())) {
            stringRedisTemplate.delete(RedisKeyUtils.getMaskMobileEmailUserKey(MaskTypeEnum.EMAIL, po.getEmail()));
        }
    }

    private String getLogoutKey(Long refreshTokenId) {
        return RedisKeyUtils.getRedisKey(LOGOUT, refreshTokenId.toString());
    }

    @SneakyThrows
    public MobileAndEmailResponse getMobileAndEmailFromRedis(MobileAndEmailRequest soaRequest, String maskMobileEmailUserKey) {
        if (StringUtils.isBlank(stringRedisTemplate.opsForValue().get(maskMobileEmailUserKey))) {
            if (null != soaRequest) {
                log.error("[GetBindOrSafeMobileService]用户不存在，req={}", JsonUtility.toJson(soaRequest));
            }
            stringRedisTemplate.expire(maskMobileEmailUserKey, setting.getBlankKeyDuration(), TimeUnit.SECONDS);
            throw new BusinessException(UserCenterErrorCode.USER_NOT_FOUND);
        }
        stringRedisTemplate.expire(maskMobileEmailUserKey, setting.getSessionTokenDuration(), TimeUnit.SECONDS);
        return JsonUtility.toObject(stringRedisTemplate.opsForValue().get(maskMobileEmailUserKey), MobileAndEmailResponse.class);
    }

    public MobileAndEmailResponse getMobileAndEmailResponseFromUserPo(UserBaseInfoPo po, String maskMobileEmailUserKey) {
        MobileAndEmailResponse response = new MobileAndEmailResponse();
        // 取消加密处理 待其他方案
        response.setUserId(po.getId().toString());
        response.setBoxingId(po.getBoxingId());
        if (StringUtils.isNotBlank(po.getPhoneNumber())) {
            response.setMobile(MaskUtil.maskPhone(po.getPhoneNumber()));
            response.setMobileCode(PhoneUtil.removeFormatAreaCode(po.getPhoneCode()));
        }
        if (StringUtils.isNotBlank(po.getSecurityPhoneNumber())) {
            response.setSecurityMobile(MaskUtil.maskPhone(po.getSecurityPhoneNumber()));
            response.setSecurityMobileCode(PhoneUtil.removeFormatAreaCode(po.getSecurityPhoneCode()));
        }
        if (StringUtils.isNotBlank(po.getEmail())) {
            response.setEmail(MaskUtil.maskEmail(po.getEmail()));
        }
        stringRedisTemplate.opsForValue().set(
                maskMobileEmailUserKey,
                JsonUtility.toJson(response),
                setting.getSessionTokenDuration(),
                TimeUnit.SECONDS);
        return response;
    }

    public ForgotPasswordValidateResponse getForgotPasswordValidateResponse(ForgotPasswordValidateRequest soaRequest, Long userId) {
        String deviceId = RequestContext.getDeviceId();
        if (BoundTypeEnum.BOUND_EMAIL.getType().equals(soaRequest.getValidType())) {
            String email = commonService.getEmailFromUserId(userId);
            validCodeUtils.check(ValidCodeCheckerTypeEnum.EMAIL, email, deviceId,
                    soaRequest.getValidCode(), ValidCodeType.FORGOT_PASSWORD_VALID);
        } else {
            String formattedPhone = commonService.getFormattedPhoneFromUserIdAndType(userId, soaRequest.getValidType());
            validCodeUtils.check(ValidCodeCheckerTypeEnum.SMS, formattedPhone, deviceId,
                    soaRequest.getValidCode(), ValidCodeType.FORGOT_PASSWORD_VALID);
        }
        String sessionKey = RedisKeyUtils.getRedisSessionKey(SessionTypeEnum.USER_FORGOT_PASSWORD_SESSION, userId.toString());
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(sessionKey))) {
            String session = stringRedisTemplate.opsForValue().get(sessionKey);
            if (!SessionUtil.checkSessionDevice(deviceId, session)) {
                log.error("[ForgotPasswordValidateService]该帐号正在找回密码过程中，请稍后再试, req={}",
                        JsonUtility.toJson(soaRequest));
                throw new BusinessException(UserCenterErrorCode.STEP_SESSION_CONFLICTED);
            }
        }
        String token = SessionUtil.generateSession(deviceId);
        stringRedisTemplate.opsForValue().set(sessionKey, token, setting.getSessionTokenDuration(), TimeUnit.SECONDS);
        log.info("[ForgotPasswordValidateService]用户忘记密码校验通过, req={}",
                JsonUtility.toJson(soaRequest));
        ForgotPasswordValidateResponse response = new ForgotPasswordValidateResponse();
        response.setUserId(soaRequest.getUserId());
        String sessionTicket = AESUtil.encrypt(SessionUtil.getSessionTicket(token), setting.getSessionTokenAesKey(), setting.getSessionTokenAesIV());
        response.setSessionTicket(sessionTicket);
        return response;
    }

    public void processForgotPassword(ForgotPasswordRequest soaRequest, Long userId) {
        String sessionKey = RedisKeyUtils.getRedisSessionKey(SessionTypeEnum.USER_FORGOT_PASSWORD_SESSION, Long.toString(userId));
        String sessionToken = stringRedisTemplate.opsForValue().get(sessionKey);
        if (!Objects.equals(AESUtil.decrypt(RequestContext.getSessionTicket(), setting.getSessionTokenAesKey(), setting.getSessionTokenAesIV()),
                SessionUtil.getSessionTicket(sessionToken))) {
            log.error("[ForgotPasswordService]找回密码时session-token已过期或不符, req={}, key=[{}], token=[{}]",
                    JsonUtility.toJson(soaRequest), sessionKey, sessionToken);
            throw new BusinessException(UserCenterErrorCode.STEP_SESSION_EXPIRED);
        }
        UserBaseInfoPo po = userBaseInfoMapper.selectById(userId);
        if (null == po) {
            log.error("[ForgotPasswordService]用户不存在, req={}", JsonUtility.toJson(soaRequest));
            throw new BusinessException(UserCenterErrorCode.USER_NOT_FOUND);
        }
        po.setPassword(BCrypt.hashpw(soaRequest.getNewPwd(), BCrypt.gensalt()));
        if (0 == userBaseInfoMapper.updateById(po)) {
            log.error("[ForgotPasswordService]找回密码失败, update failed, req={}",
                    JsonUtility.toJson(soaRequest));
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, "找回密码失败");
        }
        stringRedisTemplate.delete(sessionKey);
    }
}
