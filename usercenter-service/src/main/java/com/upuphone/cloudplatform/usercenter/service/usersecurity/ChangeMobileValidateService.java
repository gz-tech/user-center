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
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ChangeMobileValidateRequest;
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
public class ChangeMobileValidateService extends BaseService<ChangeMobileValidateRequest, SessionTicketResponse> {

    @Autowired
    private Setting setting;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ValidCodeUtils validCodeUtils;

    @Override
    protected void validate(ChangeMobileValidateRequest changeMobileValidateRequest) {
        String validCode = changeMobileValidateRequest.getValidCode();
        if (InputValidateUtil.isContainSpace(validCode) || InputValidateUtil.isContainChinese(validCode)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, VALID_CODE_MSG);
        }
        if (BoundTypeEnum.isInvalidType(changeMobileValidateRequest.getValidType())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "发送类型不合法");
        }
    }

    @Override
    protected SessionTicketResponse processCore(ChangeMobileValidateRequest soaRequest) throws Exception {
        // 修改手机号支持安全手机号/手机号/邮箱验证
        Long userId = RequestContext.getUserId();
        String deviceId = RequestContext.getDeviceId();
        if (BoundTypeEnum.BOUND_EMAIL.getType().equals(soaRequest.getValidType())) {
            String email = commonService.getEmailFromUserId(userId);
            validCodeUtils.check(ValidCodeCheckerTypeEnum.EMAIL, email, deviceId, soaRequest.getValidCode(),
                    ValidCodeType.CHANGE_BIND_MOBILE_VALID);
        } else {
            String formattedPhone = commonService.getFormattedPhoneFromUserIdAndType(userId, soaRequest.getValidType());
            validCodeUtils.check(ValidCodeCheckerTypeEnum.SMS, formattedPhone, deviceId, soaRequest.getValidCode(),
                    ValidCodeType.CHANGE_BIND_MOBILE_VALID);
        }
        String sessionKey = RedisKeyUtils.getRedisSessionKey(SessionTypeEnum.USER_CHANGE_BIND_MOBILE_SESSION, String.valueOf(userId));
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(sessionKey))) {
            String session = stringRedisTemplate.opsForValue().get(sessionKey);
            if (!SessionUtil.checkSessionDevice(deviceId, session)) {
                log.error("[ChangeMobileValidateService]该用户绑定手机号正在修改中，请稍后再试, request={}, userId=[{}]",
                        JsonUtility.toJson(soaRequest), userId);
                throw new BusinessException(UserCenterErrorCode.STEP_SESSION_CONFLICTED);
            }
        }
        String token = SessionUtil.generateSession(deviceId);
        stringRedisTemplate.opsForValue().set(sessionKey, token, setting.getSessionTokenDuration(), TimeUnit.SECONDS);
        log.info("[ChangeMobileValidateService]用户修改绑定手机号验证通过，req={}, userId=[{}], sessionKey=[{}], token=[{}]",
                JsonUtility.toJson(soaRequest), userId, sessionKey, token);
        String sessionTicket = AESUtil.encrypt(SessionUtil.getSessionTicket(token), setting.getSessionTokenAesKey(), setting.getSessionTokenAesIV());
        return SessionTicketResponse.builder().sessionTicket(sessionTicket).build();
    }
}
