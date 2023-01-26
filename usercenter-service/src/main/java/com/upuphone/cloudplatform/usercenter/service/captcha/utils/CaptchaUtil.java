package com.upuphone.cloudplatform.usercenter.service.captcha.utils;

import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.api.constant.DeviceTypeEnum;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.common.util.DateUtil;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.service.captcha.entity.CaptchaValidateBo;
import com.upuphone.cloudplatform.usercenter.service.captcha.entity.NESecretPair;
import com.upuphone.cloudplatform.usercenter.service.captcha.entity.VerifyResult;
import com.upuphone.cloudplatform.usercenter.setting.CaptchaSetting;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Classname EnvironmentDetectionUtil
 * @Description 环境检测工具类
 * @Date 2022/5/31 1:46 下午
 * @Created by gz-d
 */
@Component
@Slf4j
public class CaptchaUtil {
    @Autowired
    private CaptchaSetting captchaSetting;
    private static final long serialVersionUID = -3185301474503659058L;
    @Autowired
    private NECaptchaVerifier neCaptchaVerifier;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void captchaValidate(CaptchaValidateBo captchaValidateBo) {
        if (ifNeedCaptcha(captchaValidateBo)) {
            boolean isValid = remoteSecondValidate(captchaValidateBo.getValidate());
            if (isValid) {
                String captChaKey = RedisKeys.getCaptchaKey(captchaValidateBo.getCaptchaBusinessType().getType(),
                        captchaValidateBo.getUniqueId(), RequestContext.getDeviceId());
                stringRedisTemplate.opsForValue().increment(captChaKey);
                stringRedisTemplate.expire(captChaKey, DateUtil.getSecondsNextEarlyMorning(), TimeUnit.SECONDS);
            } else {
                throw new BusinessException(UserCenterErrorCode.CAPTCHA_VALIDATE_ERROR);
            }
        }
    }

    public boolean ifNeedCaptcha(CaptchaValidateBo captchaValidateBo) {
        String captChaKey = RedisKeys.getCaptchaKey(captchaValidateBo.getCaptchaBusinessType().getType(),
                captchaValidateBo.getUniqueId(), RequestContext.getDeviceId());
        return null == stringRedisTemplate.opsForValue().get(captChaKey);
    }

    private boolean remoteSecondValidate(String validate) {
        String user = "{'id':'123456'}";
        if (StringUtil.isEmpty(validate)) {
            throw new BusinessException(UserCenterErrorCode.CAPTCHA_VALIDATE_ERROR);
        }
        neCaptchaVerifier.setCaptchaId(captchaSetting.getCaptchaId());
        neCaptchaVerifier.setSecretPair(new NESecretPair(captchaSetting.getSecretId(), captchaSetting.getSecretKey()));
        VerifyResult result = neCaptchaVerifier.verify(validate, user); // 发起二次校验
        log.info(String.format("validate = %s,  isValid = %s , msg = %s ", validate, result.isResult(),
                result.getMsg()));
        return result.isResult();
    }
}
