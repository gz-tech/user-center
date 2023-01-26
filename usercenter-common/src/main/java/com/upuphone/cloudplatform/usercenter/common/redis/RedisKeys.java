package com.upuphone.cloudplatform.usercenter.common.redis;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;

import java.util.List;

public class RedisKeys {
    private static final String REDIS_KEY_BASE = "USERCENTER_";
    private static final String SECOND_LOGIN_KEY = REDIS_KEY_BASE + "user_login_second_%s";
    private static final String PASSWORD_ERROR_TIMES_KEY = REDIS_KEY_BASE + "password_error_times_%s";
    private static final String ACCOUNT_BLOCK_KEY = REDIS_KEY_BASE + "account_block_%s";
    private static final String CHECK_VALIDCODE_LOCK_KEY = REDIS_KEY_BASE + "check_valid_code_lock_%s_%s_%s";
    private static final String REGIST_VALID_TICKET_KEY = REDIS_KEY_BASE + "regist_valid_ticket_%s";
    private static final String LOCK_KEY = "lock_%s_%s";
    private static final String REGIST_LOCK = "regist";
    private static final String REFRESH_TOKEN_LOCK = "refresh_token";
    private static final String AUTH_CODE_LOCK = "auth_code";
    private static final String AUTH_TOKEN_LOCK = "auth_token";
    private static final String ACCESS_TOKEN_LOCK = "access_token";
    private static final String CAPTCHA_KEY = REDIS_KEY_BASE + "captcha_%s_%s_%s";

    /**
     * @param captchaType
     * @param uniqId   登录状态是userid，非登录态是手机号或邮箱
     * @param deviceId 设备Id
     * @return String key
     */
    public static String getCaptchaKey(String captchaType, String uniqId, String deviceId) {
        return String.format(CAPTCHA_KEY, captchaType, uniqId, deviceId);
    }

    public static String getUserSecondLoginKey(Long userId, String deviceId) {
        return String.format(SECOND_LOGIN_KEY, userId.toString() + deviceId);
    }

    public static String getPasswordErrorTimesKey(String userId) {
        return String.format(PASSWORD_ERROR_TIMES_KEY, userId);
    }

    public static String getAccountBlockKey(String userId) {
        return String.format(ACCOUNT_BLOCK_KEY, userId);
    }


    public static String checkValidCodeLockKey(String uniqueId, String validCode, int validCodeType) {
        return String.format(CHECK_VALIDCODE_LOCK_KEY, uniqueId, validCode, validCodeType);
    }

    public static String registTicket(String uniqueId) {
        return String.format(REGIST_VALID_TICKET_KEY, uniqueId);
    }

    public static String lockey(String type, List<String> params) {
        if (params == null || params.isEmpty()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR);
        }
        String subKey = Joiner.on("_").join(params);
        return String.format(LOCK_KEY, type, subKey);
    }

    public static String registLockKey(String uniqueId) {
        return lockey(REGIST_LOCK, Lists.newArrayList(uniqueId));
    }

    public static String refreshTokenLockKey(String refreshToken) {
        return lockey(REFRESH_TOKEN_LOCK, Lists.newArrayList(refreshToken));
    }

    public static String authCodeLockKey(String userId, String appId) {
        return lockey(AUTH_CODE_LOCK, Lists.newArrayList(userId, appId));
    }

    public static String authTokenLockKey(String userId, String appId, String deviceId) {
        return lockey(AUTH_TOKEN_LOCK, Lists.newArrayList(userId, appId, deviceId));
    }

    public static String accessTokenLockKey(String userId, String appId, String deviceId) {
        return lockey(ACCESS_TOKEN_LOCK, Lists.newArrayList(userId, appId, deviceId));
    }
}
