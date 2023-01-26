package com.upuphone.cloudplatform.usercenter.service.common.validcode;

import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.utils.SpringUtil;
import com.upuphone.cloudplatform.usercenter.common.util.DateUtil;
import com.upuphone.cloudplatform.usercenter.common.util.MaskUtil;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.constants.EmailTemplateEnum;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.remote.RemoteResponse;
import com.upuphone.cloudplatform.usercenter.remote.email.SendEmailRemoteService;
import com.upuphone.cloudplatform.usercenter.remote.email.model.SendEmailRequest;
import com.upuphone.cloudplatform.usercenter.remote.sms.SendSmsRemoteService;
import com.upuphone.cloudplatform.usercenter.remote.sms.model.SmsSendRequest;
import com.upuphone.cloudplatform.usercenter.remote.sms.model.SmsType;
import com.upuphone.cloudplatform.usercenter.service.basic.util.SmsCodeGenerator;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.checker.ValidCodeChecker;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.checker.ValidCodeCheckerTypeEnum;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.EmailSendValidCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.SendValidCodeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
@Slf4j
public class ValidCodeUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Setting setting;
    @Autowired
    private SendSmsRemoteService sendSmsRetryRemoteService;
    @Autowired
    private SendEmailRemoteService sendEmailRemoteService;
    @Autowired
    private CommonService commonService;

    private ValidCodeChecker getChecker(ValidCodeCheckerTypeEnum em) {
        return SpringUtil.getBean(em.getCheckerType(), ValidCodeChecker.class);
    }

    /**
     * 校验验证码
     *
     * @param em            检验类型 ValidCodeCheckerTypeEnum
     * @param channelCode   formattedPhoneNumber、邮箱地址 或其他类型...
     * @param deviceId      设备ID
     * @param validCode     要校验的验证码
     * @param validCodeType 验证类型 ValidCodeType
     */
    public void check(ValidCodeCheckerTypeEnum em, String channelCode, String deviceId, String validCode, ValidCodeType validCodeType) {
        getChecker(em).check(channelCode, deviceId, validCode, validCodeType);
    }


    /**
     * 根据业务计算验证码发送上限
     *
     * @param redisKey        指定发送验证码业务key(非发送上限计数key)
     * @param sendRequest     发送验证码请求体 可以为手机或邮箱
     * @param doSendValidCode 发送验证码操作Function
     * @param <T>             发送验证码请求
     * @param <R>             发送验证码远程调用结果 需实现RemoteResponse接口
     */
    @SuppressWarnings("all")
    public <T, R extends RemoteResponse> void sendAndIncrValidCodeLimit(String redisKey, T sendRequest, Function<T, R> doSendValidCode) {
        String key = redisKey + "_count";
        long count = stringRedisTemplate.opsForValue().increment(key, 1);
        if (count >= 1) {
            stringRedisTemplate.expire(key, DateUtil.getSecondsNextEarlyMorning(), TimeUnit.SECONDS);
        }
        if (count > setting.getValidCodeMaxTimes()) {
            throw new BusinessException(UserCenterErrorCode.VALIDCODE_TIMES_LIMIT);
        }
        try {
            R sendResponse = doSendValidCode.apply(sendRequest);
            if (!sendResponse.isSuccess()) {
                throw new BusinessException(UserCenterErrorCode.VALIDCODE_SEND_FAILED);
            }
        } catch (Exception e) {
            log.error("doSendValidCode failed, e=", e);
            stringRedisTemplate.opsForValue().increment(key, -1);
            throw new BusinessException(UserCenterErrorCode.VALIDCODE_SEND_FAILED);
        }
    }

    public void sendSmsRemote(SendValidCodeRequest sendValidCodeRequest) {
        String redisKey = this.getMobileValidCodeRedisKey(sendValidCodeRequest);
        SmsSendRequest smsSendRequest = new SmsSendRequest();
        smsSendRequest.setSmsType(SmsType.getByType(sendValidCodeRequest.getType().getType()));
        smsSendRequest.setReceiverPhoneNumber(sendValidCodeRequest.getPhoneNumber());
        smsSendRequest.setReceiverPhoneCode(sendValidCodeRequest.getPhoneCode());
        SmsCodeGenerator smsCodeGenerator = new SmsCodeGenerator();
        String validCode = smsCodeGenerator.generate();
        String timeLimit = (setting.getValidCodeDuration() / 60) + "";
        smsSendRequest.setParams(Arrays.asList(validCode, timeLimit));
        this.sendAndIncrValidCodeLimit(redisKey, smsSendRequest, req -> sendSmsRetryRemoteService.process(req));
        // save valid code to redis
        stringRedisTemplate.opsForValue().set(redisKey, validCode, setting.getValidCodeDuration(), TimeUnit.SECONDS);
    }

    public String getMobileValidCodeRedisKey(SendValidCodeRequest sendValidCodeRequest) {
        ValidCodeType codeType = sendValidCodeRequest.getType();
        String phoneNumberWithAreaCode = PhoneUtil.formatPhoneNumber(sendValidCodeRequest.getPhoneCode(), sendValidCodeRequest.getPhoneNumber());
        return RedisKeyUtils.getValidCodeKey(codeType, phoneNumberWithAreaCode, RequestContext.getDeviceId());
    }

    public void sendEmailRemote(EmailSendValidCodeRequest emailSendValidCodeRequest) {
        EmailTemplateEnum em = EmailTemplateEnum.getByValidCodeType(emailSendValidCodeRequest.getType());
        SmsCodeGenerator smsCodeGenerator = new SmsCodeGenerator();
        String validCode = smsCodeGenerator.generate();
        String timeLimit = (setting.getValidCodeDuration() / 60) + "";
        String text;
        if (em.isNeedAccount()) {
            UserBaseInfoPo userBaseInfo = commonService.getUserBaseInfoById(RequestContext.getUserId());
            text = String.format(em.getText(), MaskUtil.maskPhone(userBaseInfo.getEmail()), validCode, timeLimit);
        } else {
            text = String.format(em.getText(), validCode, timeLimit);
        }
        SendEmailRequest remoteRequest = SendEmailRequest.builder()
                .to(emailSendValidCodeRequest.getEmailAddress())
                .subject(em.getSubject())
                .text(text)
                .build();
        String redisKey = getEmailValidCodeRedisKey(emailSendValidCodeRequest);
        this.sendAndIncrValidCodeLimit(redisKey, remoteRequest, req -> sendEmailRemoteService.process(req));

        // save valid code to redis
        stringRedisTemplate.opsForValue().set(redisKey, validCode, setting.getValidCodeDuration(), TimeUnit.SECONDS);
    }

    public String getEmailValidCodeRedisKey(EmailSendValidCodeRequest request) {
        ValidCodeType type = request.getType();
        return RedisKeyUtils.getValidCodeKey(type, request.getEmailAddress(), RequestContext.getDeviceId());
    }
}
