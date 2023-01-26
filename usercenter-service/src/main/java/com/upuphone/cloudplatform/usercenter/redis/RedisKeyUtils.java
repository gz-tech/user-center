package com.upuphone.cloudplatform.usercenter.redis;

import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import org.apache.commons.lang3.StringUtils;

public final class RedisKeyUtils {

    private static final String REDIS_KEY_BASE = "USERCENTER_";
    // $1business $2sub-business $3params
    private static final String KEY_MODEL = REDIS_KEY_BASE + "%s_%s_%s";

    /**
     * 获取sessionKey
     *
     * @param em       SessionTypeEnum
     * @param uniqueId 唯一标识
     * @return redisKey
     */
    public static String getRedisSessionKey(SessionTypeEnum em, String uniqueId) {
        if (StringUtils.isBlank(uniqueId)) {
            return StringUtils.EMPTY;
        }
        return String.format(KEY_MODEL, "session", em.getKey(), uniqueId);
    }

    /**
     * 获取验证码相关key
     *
     * @param em       ValidCodeType
     * @param uniqueId 唯一标识
     * @return redisKey
     */
    public static String getValidCodeKey(ValidCodeType em, String uniqueId, String deviceId) {
        if (StringUtils.isBlank(uniqueId)) {
            return StringUtils.EMPTY;
        }
        return String.format(KEY_MODEL, "validcode", em.getName(), uniqueId + "_" + deviceId);
    }


    /**
     * 获取登陆唯一性锁key
     *
     * @param em       LoginTypeEnum
     * @param userId   userId
     * @param deviceId deviceId
     * @return redisKey
     */
    public static String getIdempotentCheckKey(LoginTypeEnum em, Long userId, String deviceId) {
        if (null == userId || StringUtils.isBlank(deviceId)) {
            return StringUtils.EMPTY;
        }
        return String.format(KEY_MODEL, "idempotent_check_key", em.getType(), userId.toString() + deviceId);
    }

    public static String getMaskMobileEmailUserKey(MaskTypeEnum em, String key) {
        if (StringUtils.isBlank(key)) {
            return StringUtils.EMPTY;
        }
        return String.format(KEY_MODEL, "mask_mobile_email_user", em.getType(), key);
    }

    public static String getRedisKey(String business, String key) {
        if (StringUtils.isBlank(business) || StringUtils.isBlank(key)) {
            return StringUtils.EMPTY;
        }
        return REDIS_KEY_BASE + business + "_" + key;
    }

    public static String getRedisKey(String business, String subBusiness, String... keys) {
        if (StringUtils.isBlank(business) || StringUtils.isBlank(subBusiness)) {
            return StringUtils.EMPTY;
        }
        return String.format(KEY_MODEL, business, subBusiness, StringUtils.join(keys, "_"));
    }
}
