package com.upuphone.cloudplatform.usercenter.service.basic;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.constants.BoundTypeEnum;
import com.upuphone.cloudplatform.usercenter.constants.CaptchaBusinessEnum;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.remote.sms.model.SmsType;
import com.upuphone.cloudplatform.usercenter.service.captcha.entity.CaptchaValidateBo;
import com.upuphone.cloudplatform.usercenter.service.captcha.utils.CaptchaUtil;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.ValidCodeUtils;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.SendValidCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.SendValidCodeWithoutPhoneRequest;
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
 * Created: 2022/1/25
 */
@Service
@Slf4j
public class SendValidCodeWithoutPhoneService extends BaseService<SendValidCodeWithoutPhoneRequest, Boolean> {

    @Autowired
    private ValidCodeUtils validCodeUtils;
    @Autowired
    private CommonService commonService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CaptchaUtil captchaUtil;

    private static final List<ValidCodeType> CAPTCHA_VALID_TYPE = new ArrayList<>();

    static {
        CAPTCHA_VALID_TYPE.addAll(Arrays.asList(ValidCodeType.CHANGE_BIND_MOBILE_VALID, ValidCodeType.CHANGE_SECURITY_MOBILE_VALID,
                FORGOT_PASSWORD_VALID, CHANGE_EMAIL_VALID));
    }

    @Override
    protected void validate(SendValidCodeWithoutPhoneRequest request) {
        if (BoundTypeEnum.isInvalidType(request.getPhoneType())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "接收方类型不合法");
        }
        if (SmsType.isInvalidType(request.getType().getType())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "发送类型不合法");
        }
        if (ValidCodeType.isInvalidTypeForWithoutPhoneNumber(request.getType())) {
            throw new BusinessException(UserCenterErrorCode.VALIDCODE_CHECK_NOT_SUPPORTED);
        }
    }

    @Override
    protected Boolean processCore(SendValidCodeWithoutPhoneRequest request) {
        if (CAPTCHA_VALID_TYPE.contains(request.getType())) {
            CaptchaValidateBo captchaValidateBo = new CaptchaValidateBo();
            captchaValidateBo.setValidate(request.getValidate());
            captchaValidateBo.setUniqueId(request.getUserId().toString());
            captchaValidateBo.setCaptchaBusinessType(CaptchaBusinessEnum.SECURITY_MODULE);
            captchaUtil.captchaValidate(captchaValidateBo);
        }
        String formatted = commonService.getFormattedPhoneFromUserIdAndType(request.getUserId(), request.getPhoneType());
        String phoneNumber = PhoneUtil.getPhoneFromFormatted(formatted);
        String phoneCode = PhoneUtil.getAreaCodeFromFormatted(formatted);
        SendValidCodeRequest sendValidCodeRequest = new SendValidCodeRequest();
        sendValidCodeRequest.setPhoneNumber(phoneNumber);
        sendValidCodeRequest.setPhoneCode(phoneCode);
        sendValidCodeRequest.setType(request.getType());
        // 检证是否满足最小发送间隔
        String redisKey = validCodeUtils.getMobileValidCodeRedisKey(sendValidCodeRequest);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            log.error("距离上次发送验证码间隔过短，请稍后再试, req={}", JsonUtility.toJson(sendValidCodeRequest));
            throw new BusinessException(UserCenterErrorCode.VALIDCODE_SEND_GAP_TOO_SHORT);
        }
        validCodeUtils.sendSmsRemote(sendValidCodeRequest);
        return true;
    }
}
