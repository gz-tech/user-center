package com.upuphone.cloudplatform.usercenter.service.basic;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.common.util.MaskUtil;
import com.upuphone.cloudplatform.usercenter.constants.CaptchaBusinessEnum;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.service.captcha.entity.CaptchaValidateBo;
import com.upuphone.cloudplatform.usercenter.service.captcha.utils.CaptchaUtil;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.ValidCodeUtils;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.EmailSendValidCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.EmailSendValidCodeWithoutAddressRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.EmailSendValidCodeWithoutAddressResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.upuphone.cloudplatform.usercenter.constants.ValidCodeType.CHANGE_EMAIL_VALID;
import static com.upuphone.cloudplatform.usercenter.constants.ValidCodeType.FORGOT_PASSWORD_VALID;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/4
 */
@Service
@Slf4j
public class EmailSendValidCodeWithoutAddressService extends BaseService<EmailSendValidCodeWithoutAddressRequest,
        EmailSendValidCodeWithoutAddressResponse> {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ValidCodeUtils validCodeUtils;
    @Autowired
    private CaptchaUtil captchaUtil;

    private static final List<ValidCodeType> CAPTCHA_VALID_TYPE = new ArrayList<>();

    static {
        CAPTCHA_VALID_TYPE.addAll(Arrays.asList(ValidCodeType.CHANGE_BIND_MOBILE, ValidCodeType.CHANGE_SECURITY_MOBILE_VALID,
                FORGOT_PASSWORD_VALID, CHANGE_EMAIL_VALID));
    }

    @Override
    protected void validate(EmailSendValidCodeWithoutAddressRequest request) {
        if (ValidCodeType.isInvalidTypeForWithoutEmailAddress(request.getType())) {
            throw new BusinessException(UserCenterErrorCode.VALIDCODE_CHECK_NOT_SUPPORTED);
        }
    }

    @Override
    protected EmailSendValidCodeWithoutAddressResponse processCore(EmailSendValidCodeWithoutAddressRequest request) throws Exception {
        if (CAPTCHA_VALID_TYPE.contains(request.getType())) {
            CaptchaValidateBo captchaValidateBo = new CaptchaValidateBo();
            captchaValidateBo.setValidate(request.getValidate());
            captchaValidateBo.setUniqueId(request.getUserId().toString());
            captchaValidateBo.setCaptchaBusinessType(CaptchaBusinessEnum.SECURITY_MODULE);
            captchaUtil.captchaValidate(captchaValidateBo);
        }
        String email = commonService.getEmailFromUserId(request.getUserId());
        EmailSendValidCodeRequest sendRequest = new EmailSendValidCodeRequest();
        sendRequest.setEmailAddress(email);
        sendRequest.setType(request.getType());
        String redisKey = validCodeUtils.getEmailValidCodeRedisKey(sendRequest);
        // 检证是否小于最小间隔
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            log.error("距离上次发送验证码间隔过短，请稍后再试, req={}", JsonUtility.toJson(request));
            throw new BusinessException(UserCenterErrorCode.VALIDCODE_SEND_GAP_TOO_SHORT);
        }
        validCodeUtils.sendEmailRemote(sendRequest);
        EmailSendValidCodeWithoutAddressResponse response = new EmailSendValidCodeWithoutAddressResponse();
        response.setEmailAddress(MaskUtil.maskEmail(email));
        return response;
    }
}
