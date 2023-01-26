package com.upuphone.cloudplatform.usercenter.service.authentication.util;

import com.upuphone.cloudplatform.common.utils.DateTimeUtil;
import com.upuphone.cloudplatform.usercenter.entity.DeviceInfo;
import com.upuphone.cloudplatform.usercenter.entity.token.TokenBo;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LastRefreshTokenUtil {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_REFRESH_TOKEN = "REFRESH_TOKEN";

    private static final String BEFORE_REFRESH_TOKEN = "BEFORE";

    private static final String REDIS_ACCESS_TOKEN = "ACCESS_TOKEN";

    private static final String NEW_ACCESS_TOKEN = "NEW";


    /**
     * 判断是否为缓存中上一次生成的refreshToken
     *
     * @param requestTokenBO requestTokenBO
     * @param refreshToken   refreshToken
     * @return true-是
     */
    public boolean isLastReqRefreshTokenInRedis(TokenBo requestTokenBO, String refreshToken) {
        String lastRefreshTokenRedisKey = RedisKeyUtils.getRedisKey(REDIS_REFRESH_TOKEN, BEFORE_REFRESH_TOKEN,
                requestTokenBO.getAppid(), requestTokenBO.getDeviceId(),
                requestTokenBO.getDeviceType(), requestTokenBO.getUserId());
        String lastRefreshTokenRedisValue = redisTemplate.opsForValue().get(lastRefreshTokenRedisKey);
        return StringUtils.equals(lastRefreshTokenRedisValue, refreshToken);
    }

    /**
     * 更新缓存中上一次生成的refreshToken
     *
     * @param requestTokenBO requestTokenBO
     * @param expireAt       过期时间点=新的accessToken过期时间点
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(value = 2000, multiplier = 1.5))
    public void updateLastRequestRefreshTokenInRedis(TokenBo requestTokenBO, LocalDateTime expireAt) {
        String lastRefreshTokenRedisKey = RedisKeyUtils.getRedisKey(REDIS_REFRESH_TOKEN, BEFORE_REFRESH_TOKEN,
                requestTokenBO.getAppid(), requestTokenBO.getDeviceId(),
                requestTokenBO.getDeviceType(), requestTokenBO.getUserId());
        redisTemplate.opsForValue().set(lastRefreshTokenRedisKey, requestTokenBO.getTokenStr());
        redisTemplate.expireAt(lastRefreshTokenRedisKey, DateTimeUtil.localDateTimeToDate(expireAt));
    }

    /**
     * 更新缓存中的accessToken
     *
     * @param deviceInfo  设备信息
     * @param userId      userId
     * @param accessToken accessToken
     * @param expireAt    过期时间点
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(value = 2000, multiplier = 1.5))
    public void setNewAccessTokenInRedis(DeviceInfo deviceInfo, Long userId,
            String accessToken, LocalDateTime expireAt) {
        String newAccessTokenInRedisKey = RedisKeyUtils.getRedisKey(REDIS_ACCESS_TOKEN, NEW_ACCESS_TOKEN,
                deviceInfo.getAppId(), deviceInfo.getDeviceId(), deviceInfo.getDeviceType(), userId.toString());
        redisTemplate.opsForValue().set(newAccessTokenInRedisKey, accessToken);
        redisTemplate.expireAt(newAccessTokenInRedisKey, DateTimeUtil.localDateTimeToDate(expireAt));
    }

    /**
     * 获取缓存中最新的accessToken
     *
     * @param deviceInfo 设备信息
     * @param userId     userId
     * @return accessToken
     */
    public String getNewAccessTokenInRedis(DeviceInfo deviceInfo, Long userId) {
        String newAccessTokenInRedisKey = RedisKeyUtils.getRedisKey(REDIS_ACCESS_TOKEN, NEW_ACCESS_TOKEN,
                deviceInfo.getAppId(), deviceInfo.getDeviceId(), deviceInfo.getDeviceType(), userId + "");
        return redisTemplate.opsForValue().get(newAccessTokenInRedisKey);
    }
}
