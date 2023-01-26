package com.upuphone.cloudplatform.usercenter.service.authentication.util;

import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.auth.vo.AuthErrorCode;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.service.authentication.model.AuthCodeBo;
import com.upuphone.cloudplatform.usercenter.service.oauth.util.RandomValueStringGenerator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/28
 */
@Component
@Slf4j
public class AuthCodeUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String BIZ_KEY = "authentication";
    private static final String SUB_BIZ_KEY = "authCode";

    public String getCodeRedisKey(String code, String func) {
        return RedisKeyUtils.getRedisKey(BIZ_KEY, SUB_BIZ_KEY, code, func);
    }

    @SneakyThrows
    public String generateIfAbsent(String scope, Integer codeValidity) {
        String code = new RandomValueStringGenerator().generate();
        AuthCodeBo bo = new AuthCodeBo();
        bo.setCode(code);
        bo.setScope(scope);
        bo.setAppId(RequestContext.getAppId());
        bo.setDeviceId(RequestContext.getDeviceId());
        bo.setDeviceName(RequestContext.getDeviceName());
        bo.setModel(RequestContext.getModel());
        bo.setDeviceType(RequestContext.getDeviceType());
        bo.setUserId(RequestContext.getUserId());
        String json = JsonUtility.toJson(bo);
        // 此处也应该存储scope 即存储一个对象
        String redisKey = this.getCodeRedisKey(code, "user");
        stringRedisTemplate.opsForValue().set(redisKey, json, codeValidity, TimeUnit.SECONDS);
        return code;
    }

    @SneakyThrows
    public AuthCodeBo getCodeInfo(String code, String func) {
        String redisKey = this.getCodeRedisKey(code, func);
        String json = stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(json)) {
            throw new BusinessException(AuthErrorCode.CODE_EXPIRED);
        }
        return JsonUtility.toObject(json, AuthCodeBo.class);
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(value = 2000, multiplier = 1.5))
    public void delCode(String code, String func) {
        String redisKey = this.getCodeRedisKey(code, func);
        stringRedisTemplate.delete(redisKey);
    }
}
