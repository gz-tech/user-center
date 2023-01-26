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
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ChangeEmailValidRequest;
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
 * Created: 2022/3/4
 */
@Service
@Slf4j
public class ChangeEmailValidateService extends BaseService<ChangeEmailValidRequest, SessionTicketResponse> {

    @Autowired
    private CommonService commonService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Setting setting;
    @Autowired
    private ValidCodeUtils validCodeUtils;

    @Override
    protected void validate(ChangeEmailValidRequest changeEmailValidRequest) {
        String validCode = changeEmailValidRequest.getValidCode();
        if (InputValidateUtil.isContainSpace(validCode) || InputValidateUtil.isContainChinese(validCode)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, VALID_CODE_MSG);
        }
        if (BoundTypeEnum.isInvalidType(changeEmailValidRequest.getValidType())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "绑定类型不支持");
        }
    }

    @Override
    protected SessionTicketResponse processCore(ChangeEmailValidRequest changeEmailValidRequest) throws Exception {
        // 修改/绑定邮箱-支持安全手机、手机、邮箱验证
        Long userId = RequestContext.getUserId();
        String deviceId = RequestContext.getDeviceId();
        if (BoundTypeEnum.BOUND_EMAIL.getType().equals(changeEmailValidRequest.getValidType())) {
            String email = commonService.getEmailFromUserId(userId);
            validCodeUtils.check(ValidCodeCheckerTypeEnum.EMAIL, email, deviceId, changeEmailValidRequest.getValidCode(),
                    ValidCodeType.CHANGE_EMAIL_VALID);
        } else {
            String formattedPhone = commonService.getFormattedPhoneFromUserIdAndType(userId,
                    changeEmailValidRequest.getValidType());
            validCodeUtils.check(ValidCodeCheckerTypeEnum.SMS, formattedPhone, deviceId, changeEmailValidRequest.getValidCode(),
                    ValidCodeType.CHANGE_EMAIL_VALID);
        }
        String sessionKey = RedisKeyUtils.getRedisSessionKey(SessionTypeEnum.USER_CHANGE_EMAIL_SESSION, userId.toString());
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(sessionKey))) {
            String session = stringRedisTemplate.opsForValue().get(sessionKey);
            if (!SessionUtil.checkSessionDevice(deviceId, session)) {
                log.error("[EmailChangeValidateService]该用户绑定邮箱正在修改中，请稍后再试, request={}, userId=[{}]",
                        JsonUtility.toJson(changeEmailValidRequest), userId);
                throw new BusinessException(UserCenterErrorCode.STEP_SESSION_CONFLICTED);
            }
        }
        String token = SessionUtil.generateSession(deviceId);
        stringRedisTemplate.opsForValue().set(sessionKey, token, setting.getSessionTokenDuration(), TimeUnit.SECONDS);
        log.info("[ChangeMobileValidateService]用户修改绑定邮箱验证通过，req={}, userId=[{}], sessionKey=[{}], token=[{}]",
                JsonUtility.toJson(changeEmailValidRequest), userId, sessionKey, token);
        String sessionTicket = AESUtil.encrypt(SessionUtil.getSessionTicket(token), setting.getSessionTokenAesKey(), setting.getSessionTokenAesIV());
        return SessionTicketResponse.builder().sessionTicket(sessionTicket).build();
    }
}
