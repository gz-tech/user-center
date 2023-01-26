package com.upuphone.cloudplatform.usercenter.service.basic;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.constants.CaptchaBusinessEnum;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.service.captcha.entity.CaptchaValidateBo;
import com.upuphone.cloudplatform.usercenter.service.captcha.utils.CaptchaUtil;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.ValidCodeUtils;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.validate.ChangeEmailStrategy;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.validate.EmailValidCodeRegistStrategy;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.EmailSendValidCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.EmailSendValidCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/4
 */
@Service
@Slf4j
public class EmailSendValidCodeService extends BaseService<EmailSendValidCodeRequest, EmailSendValidCodeResponse> {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ValidCodeUtils validCodeUtils;
    @Autowired
    private ChangeEmailStrategy changeEmailStrategy;
    @Autowired
    private EmailValidCodeRegistStrategy emailValidCodeRegistStrategy;
    @Autowired
    private CaptchaUtil captchaUtil;

    @Override
    protected void validate(EmailSendValidCodeRequest emailSendValidCodeRequest) {
        if (ValidCodeType.isInvalidTypeForEmailAddress(emailSendValidCodeRequest.getType())) {
            throw new BusinessException(UserCenterErrorCode.VALIDCODE_CHECK_NOT_SUPPORTED);
        }
    }

    @Override
    protected EmailSendValidCodeResponse processCore(EmailSendValidCodeRequest emailSendValidCodeRequest) throws Exception {
        if (Objects.equals(emailSendValidCodeRequest.getType(), ValidCodeType.REGISTER)) {
            CaptchaValidateBo captchaValidateBo = new CaptchaValidateBo();
            captchaValidateBo.setValidate(emailSendValidCodeRequest.getValidate());
            captchaValidateBo.setUniqueId(emailSendValidCodeRequest.getEmailAddress());
            captchaValidateBo.setCaptchaBusinessType(CaptchaBusinessEnum.REGIESTER_MODULE);
            //滑块安全验证
            captchaUtil.captchaValidate(captchaValidateBo);
            //判断邮箱是否已注册
            emailValidCodeRegistStrategy.validate(emailSendValidCodeRequest);
        }
        String redisKey = validCodeUtils.getEmailValidCodeRedisKey(emailSendValidCodeRequest);
        // 检证是否小于最小间隔
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            log.error("距离上次发送验证码间隔过短，请稍后再试, req={}", JsonUtility.toJson(emailSendValidCodeRequest));
            throw new BusinessException(UserCenterErrorCode.VALIDCODE_SEND_GAP_TOO_SHORT);
        }
        // 提前验证邮箱是否被绑定
        if (emailSendValidCodeRequest.getType() == ValidCodeType.CHANGE_EMAIL) {
            changeEmailStrategy.validate(emailSendValidCodeRequest);
        }
        validCodeUtils.sendEmailRemote(emailSendValidCodeRequest);
        // 构造 response
        EmailSendValidCodeResponse response = new EmailSendValidCodeResponse();
        response.setEmailAddress(emailSendValidCodeRequest.getEmailAddress());
        return response;
    }
}
