package com.upuphone.cloudplatform.usercenter.service.userbasic.register;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.util.TicketUtil;
import com.upuphone.cloudplatform.usercenter.setting.RegisterSetting;
import com.upuphone.cloudplatform.usercenter.vo.request.register.CheckEmailCodeForRegistRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.register.CheckEmailCodeForRegistResponse;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CheckEmailCodeRegistService extends BaseService<CheckEmailCodeForRegistRequest, CheckEmailCodeForRegistResponse> {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TicketUtil ticketUtil;

    @Autowired
    private RegisterSetting registerSetting;

    @Override
    protected void validate(CheckEmailCodeForRegistRequest request) {
        if (null == RequestContext.getDeviceId()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "device id get error");
        }
    }

    @Override
    protected CheckEmailCodeForRegistResponse processCore(CheckEmailCodeForRegistRequest soaRequest) throws Exception {

        String lockKey = RedisKeys.registLockKey(soaRequest.getEmailAddress());
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(0, 10, TimeUnit.SECONDS)) {
                throw new BusinessException(CommonErrorCode.OPERATION_ERROR, "please not repeat request");
            }

            String redisKey = RedisKeyUtils.getValidCodeKey(ValidCodeType.REGISTER, soaRequest.getEmailAddress(), RequestContext.getDeviceId());
            ValueOperations<String, String> validCodeRedisOpt = stringRedisTemplate.opsForValue();
            String validCodeInRedis = validCodeRedisOpt.get(redisKey);
            if (!soaRequest.getValidCode().equals(validCodeInRedis)) {
                throw new BusinessException(CommonErrorCode.PARAM_ERROR, "valid code wrong");
            }

            String ticket = ticketUtil.generateTicket(RequestContext.getDeviceId(), null,
                    soaRequest.getEmailAddress(), validCodeInRedis);

            String ticketKey = RedisKeys.registTicket(soaRequest.getEmailAddress());
            stringRedisTemplate.opsForValue().set(ticketKey, ticket);
            stringRedisTemplate.expire(ticketKey, registerSetting.getRegistTicketValidDuration(), TimeUnit.SECONDS);

            CheckEmailCodeForRegistResponse result = new CheckEmailCodeForRegistResponse();
            result.setTicket(ticket);

            // remove valid code from redis
            stringRedisTemplate.delete(redisKey);

            return result;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
