package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.common.util.SessionUtil;
import com.upuphone.cloudplatform.usercenter.common.util.encryption.AESUtil;
import com.upuphone.cloudplatform.usercenter.constants.CaptchaBusinessEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.redis.SessionTypeEnum;
import com.upuphone.cloudplatform.usercenter.service.captcha.entity.CaptchaValidateBo;
import com.upuphone.cloudplatform.usercenter.service.captcha.utils.CaptchaUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.util.PasswordUtil;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ResetPasswordValidRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.SessionTicketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/27
 */
@Service
@Slf4j
public class ResetPasswordValidateService extends BaseService<ResetPasswordValidRequest, SessionTicketResponse> {

    @Autowired
    private Setting setting;
    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PasswordUtil passwordUtil;
    @Autowired
    private CaptchaUtil captchaUtil;

    @Override
    protected void validate(ResetPasswordValidRequest request) {

    }

    @Override
    protected SessionTicketResponse processCore(ResetPasswordValidRequest soaRequest) throws Exception {
        CaptchaValidateBo captchaValidateBo = new CaptchaValidateBo();
        captchaValidateBo.setValidate(soaRequest.getValidate());
        captchaValidateBo.setCaptchaBusinessType(CaptchaBusinessEnum.SECURITY_MODULE);
        captchaValidateBo.setUniqueId(RequestContext.getUserId().toString());
        captchaUtil.captchaValidate(captchaValidateBo);

        Long userId = RequestContext.getUserId();
        UserBaseInfoPo po = userBaseInfoMapper.selectById(userId);
        passwordUtil.checkPassword(po.getId(), soaRequest.getOriginPwd(), po.getPassword());
        String sessionKey = RedisKeyUtils.getRedisSessionKey(SessionTypeEnum.USER_CHANGE_PASSWORD_SESSION, String.valueOf(userId));
        String deviceId = RequestContext.getDeviceId();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(sessionKey))) {
            String session = stringRedisTemplate.opsForValue().get(sessionKey);
            if (!SessionUtil.checkSessionDevice(deviceId, session)) {
                log.error("[ResetPasswordValidateService]该用户密码正在修改中，请稍后再试, request={}, userId=[{}]",
                        JsonUtility.toJson(soaRequest), userId);
                throw new BusinessException(UserCenterErrorCode.STEP_SESSION_CONFLICTED);
            }
        }
        String token = SessionUtil.generateSession(deviceId);
        stringRedisTemplate.opsForValue().set(sessionKey, token, setting.getSessionTokenDuration(), TimeUnit.SECONDS);
        log.info("[ResetPasswordValidateService]用户修改密码验证通过，req={}, userId=[{}], sessionKey=[{}], token=[{}]",
                JsonUtility.toJson(soaRequest), userId, sessionKey, token);
        String sessionTicket = AESUtil.encrypt(SessionUtil.getSessionTicket(token), setting.getSessionTokenAesKey(), setting.getSessionTokenAesIV());
        return SessionTicketResponse.builder().sessionTicket(sessionTicket).build();
    }
}
