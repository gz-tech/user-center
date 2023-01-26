package com.upuphone.cloudplatform.usercenter.service.userbasic.register;

import com.google.common.base.Strings;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.common.util.StringUtil;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.util.TicketUtil;
import com.upuphone.cloudplatform.usercenter.setting.RegisterSetting;
import com.upuphone.cloudplatform.usercenter.vo.request.register.CheckPhoneCodeForRegistRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.register.CheckPhoneCodeForRegistResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CheckPhoneCodeRegistService extends BaseService<CheckPhoneCodeForRegistRequest, CheckPhoneCodeForRegistResponse> {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TicketUtil ticketUtil;

    @Autowired
    private RegisterSetting registerSetting;


    @Override
    protected void validate(CheckPhoneCodeForRegistRequest checkPhoneCodeForRegistRequest) {
        if (null == RequestContext.getDeviceId()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "device id get error");
        }
        if (!Strings.isNullOrEmpty(checkPhoneCodeForRegistRequest.getPhoneNumber())) {
            checkPhoneCodeForRegistRequest
                    .setPhoneNumber(StringUtil.replaceSpace(checkPhoneCodeForRegistRequest.getPhoneNumber()));
        }
    }

    @Override
    protected CheckPhoneCodeForRegistResponse processCore(CheckPhoneCodeForRegistRequest checkPhoneCodeForRegistRequest) throws Exception {
        String phoneAreaCode = PhoneUtil.formatPhoneAreaCode(checkPhoneCodeForRegistRequest.getPhoneCode());
        String phoneNumber = checkPhoneCodeForRegistRequest.getPhoneNumber();

        String phoneNumberWithAreaCode = phoneAreaCode + phoneNumber;
        String lockKey = RedisKeys.checkValidCodeLockKey(phoneNumberWithAreaCode, checkPhoneCodeForRegistRequest.getValidCode(),
                ValidCodeType.REGISTER.getType());
        Boolean lockSuccess = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 30, TimeUnit.SECONDS);
        if (lockSuccess == null || !lockSuccess) {
            throw new BusinessException(CommonErrorCode.OPERATION_ERROR, "please not repeat request");
        }
        CheckPhoneCodeForRegistResponse result = new CheckPhoneCodeForRegistResponse();
        try {

            String redisKey = RedisKeyUtils.getValidCodeKey(ValidCodeType.REGISTER, phoneNumberWithAreaCode, RequestContext.getDeviceId());
            ValueOperations<String, String> validCodeRedisOpt = stringRedisTemplate.opsForValue();
            String validCodeInRedis = validCodeRedisOpt.get(redisKey);
            if (!checkPhoneCodeForRegistRequest.getValidCode().equals(validCodeInRedis)) {
                throw new BusinessException(UserCenterErrorCode.VALIDCODE_ERROR);
            }
            if (null == RequestContext.getDeviceId()) {
                throw new BusinessException(CommonErrorCode.PARAM_ERROR, "device id get error");
            }

            String ticket = ticketUtil.generateTicket(RequestContext.getDeviceId(), phoneNumberWithAreaCode, null, validCodeInRedis);

            String ticketKey = RedisKeys.registTicket(phoneNumberWithAreaCode);
            stringRedisTemplate.opsForValue().set(ticketKey, ticket);
            stringRedisTemplate.expire(ticketKey, registerSetting.getRegistTicketValidDuration(), TimeUnit.SECONDS);
            result.setTicket(ticket);

            stringRedisTemplate.delete(redisKey);// remove valid code from redis
        } finally {
            stringRedisTemplate.delete(lockKey);
        }

        return result;
    }
}
