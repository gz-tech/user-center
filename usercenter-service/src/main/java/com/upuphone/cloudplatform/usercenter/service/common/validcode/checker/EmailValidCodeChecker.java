package com.upuphone.cloudplatform.usercenter.service.common.validcode.checker;

import com.google.common.collect.Sets;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class EmailValidCodeChecker extends AbstractValidCodeChecker {

    private static final Set<ValidCodeType> SUPPORT_VALID_CODE_TYPES = Sets.newHashSet(
            ValidCodeType.REGISTER,
            ValidCodeType.LOGIN,
            ValidCodeType.CHANGE_SECURITY_MOBILE_VALID,
            ValidCodeType.CHANGE_BIND_MOBILE_VALID,
            ValidCodeType.CHANGE_EMAIL_VALID,
            ValidCodeType.CHANGE_EMAIL,
            ValidCodeType.FORGOT_PASSWORD_VALID,
            ValidCodeType.NEW_DEVICE_EMAIL,
            ValidCodeType.SDK_VALIDATE);
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected boolean checkValidCodeType(ValidCodeType validCodeType) {
        return SUPPORT_VALID_CODE_TYPES.contains(validCodeType);
    }

    @Override
    protected void doCheck(String channelCode, String deviceId, String validCode, ValidCodeType validCodeType) {
        String redisKey = RedisKeyUtils.getValidCodeKey(validCodeType, channelCode, deviceId);
        String valueInRedis = stringRedisTemplate.opsForValue().get(redisKey);
        if (!Objects.equals(valueInRedis, validCode)) {
            log.error("邮箱[{}]的邮箱验证码验证失败, validCode=[{}], value in redis=[{}]", channelCode, validCode, valueInRedis);
            throw new BusinessException(UserCenterErrorCode.VALIDCODE_ERROR);
        }
    }
}
