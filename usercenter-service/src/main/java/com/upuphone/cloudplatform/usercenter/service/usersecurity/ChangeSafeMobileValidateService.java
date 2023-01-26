package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.common.util.SessionUtil;
import com.upuphone.cloudplatform.usercenter.common.util.encryption.AESUtil;
import com.upuphone.cloudplatform.usercenter.constants.BoundTypeEnum;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.redis.SessionTypeEnum;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.ValidCodeUtils;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.checker.ValidCodeCheckerTypeEnum;
import com.upuphone.cloudplatform.usercenter.service.util.InputValidateUtil;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ChangeSafeMobileValidateRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.SessionTicketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.VALID_CODE_MSG;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/23
 */
@Service
@Slf4j
public class ChangeSafeMobileValidateService extends BaseService<ChangeSafeMobileValidateRequest, SessionTicketResponse> {

    @Autowired
    private Setting setting;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ValidCodeUtils validCodeUtils;

    @Override
    protected void validate(ChangeSafeMobileValidateRequest changeSafeMobileValidateRequest) {
        String validCode = changeSafeMobileValidateRequest.getValidCode();
        if (InputValidateUtil.isContainSpace(validCode) || InputValidateUtil.isContainChinese(validCode)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, VALID_CODE_MSG);
        }
        if (BoundTypeEnum.isInvalidType(changeSafeMobileValidateRequest.getValidType())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "发送类型不合法");
        }
    }

    @Override
    protected SessionTicketResponse processCore(ChangeSafeMobileValidateRequest soaRequest) throws Exception {
        Long userId = RequestContext.getUserId();
        String deviceId = RequestContext.getDeviceId();
        // 修改安全手机号时，验证的是当前绑定安全手机号/手机号/邮箱
        if (BoundTypeEnum.BOUND_EMAIL.getType().equals(soaRequest.getValidType())) {
            String email = commonService.getEmailFromUserId(userId);
            validCodeUtils.check(ValidCodeCheckerTypeEnum.EMAIL, email, deviceId, soaRequest.getValidCode(),
                    ValidCodeType.CHANGE_SECURITY_MOBILE_VALID);
        } else {
            String formattedPhone = commonService.getFormattedPhoneFromUserIdAndType(userId, soaRequest.getValidType());
            validCodeUtils.check(ValidCodeCheckerTypeEnum.SMS, formattedPhone, deviceId, soaRequest.getValidCode(),
                    ValidCodeType.CHANGE_SECURITY_MOBILE_VALID);
        }
        String sessionKey = RedisKeyUtils.getRedisSessionKey(SessionTypeEnum.USER_CHANGE_SECURITY_MOBILE_SESSION, String.valueOf(userId));
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(sessionKey))) {
            String session = stringRedisTemplate.opsForValue().get(sessionKey);
            if (!SessionUtil.checkSessionDevice(deviceId, session)) {
                log.error("[ChangeSafeMobileValidateService]该帐号安全手机号正在修改过程中，暂时不能修改, req={}, userId=[{}]",
                        JsonUtility.toJson(soaRequest), userId);
                throw new BusinessException(UserCenterErrorCode.STEP_SESSION_CONFLICTED);
            }
        }
        String token = SessionUtil.generateSession(deviceId);
        stringRedisTemplate.opsForValue().set(sessionKey, token, setting.getSessionTokenDuration(), TimeUnit.SECONDS);
        log.info("[ChangeSafeMobileValidateService]用户修改安全手机号验证通过，req={}, userId=[{}], sessionKey=[{}], token=[{}]",
                JsonUtility.toJson(soaRequest), userId, sessionKey, token);
        String sessionTicket = AESUtil.encrypt(SessionUtil.getSessionTicket(token), setting.getSessionTokenAesKey(), setting.getSessionTokenAesIV());
        return SessionTicketResponse.builder().sessionTicket(sessionTicket).build();
    }
}
