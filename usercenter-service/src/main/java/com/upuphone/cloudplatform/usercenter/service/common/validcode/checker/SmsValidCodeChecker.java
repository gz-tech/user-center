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
public class SmsValidCodeChecker extends AbstractValidCodeChecker {

    private static final Set<ValidCodeType> SUPPORT_VALID_CODE_TYPES = Sets.newHashSet(
            ValidCodeType.REGISTER,
            ValidCodeType.LOGIN,
            ValidCodeType.RESET_PASSWORD,
            ValidCodeType.CHANGE_SECURITY_MOBILE_VALID,
            ValidCodeType.CHANGE_SECURITY_MOBILE,
            ValidCodeType.CHANGE_BIND_MOBILE_VALID,
            ValidCodeType.CHANGE_BIND_MOBILE,
            ValidCodeType.CHANGE_EMAIL_VALID,
            ValidCodeType.FORGOT_PASSWORD_VALID,
            ValidCodeType.SECURITY_PHONE_LOGIN,
            ValidCodeType.NEW_DEVICE_PHONE_LOGIN,
            ValidCodeType.SDK_VALIDATE,
            ValidCodeType.REGISTER_LOGIN_COMBINE);
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
            log.error("号码[{}]的手机验证码验证失败, validCode=[{}], value in redis=[{}]", channelCode, validCode, valueInRedis);
            throw new BusinessException(UserCenterErrorCode.VALIDCODE_ERROR);
        }
    }
}
