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
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.BindSafeMobileValidateRequest;
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
public class BindSafeMobileValidateService extends BaseService<BindSafeMobileValidateRequest, SessionTicketResponse> {

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
    protected void validate(BindSafeMobileValidateRequest request) {
    }

    @Override
    protected SessionTicketResponse processCore(BindSafeMobileValidateRequest soaRequest) throws Exception {
        CaptchaValidateBo captchaValidateBo = new CaptchaValidateBo();
        captchaValidateBo.setValidate(soaRequest.getValidate());
        captchaValidateBo.setCaptchaBusinessType(CaptchaBusinessEnum.SECURITY_MODULE);
        captchaValidateBo.setUniqueId(RequestContext.getUserId().toString());
        captchaUtil.captchaValidate(captchaValidateBo);

        Long userId = RequestContext.getUserId();
        String deviceId = RequestContext.getDeviceId();
        String sessionKey = RedisKeyUtils.getRedisSessionKey(SessionTypeEnum.USER_BIND_SECURITY_MOBILE_SESSION, String.valueOf(userId));
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(sessionKey))) {
            String session = stringRedisTemplate.opsForValue().get(sessionKey);
            if (!SessionUtil.checkSessionDevice(deviceId, session)) {
                log.error("[BindSafeMobileValidateService]安全手机号正在修改中，请稍后再试, userId=[{}], deviceId=[{}]", userId, deviceId);
                throw new BusinessException(UserCenterErrorCode.STEP_SESSION_CONFLICTED);
            }
        }
        UserBaseInfoPo po = userBaseInfoMapper.selectById(userId);
        passwordUtil.checkPassword(po.getId(), soaRequest.getPassword(), po.getPassword());
        String token = SessionUtil.generateSession(deviceId);
        stringRedisTemplate.opsForValue().set(sessionKey, token, setting.getSessionTokenDuration(), TimeUnit.SECONDS);
        log.info("[BindSafeMobileValidateService]用户绑定安全手机号验证通过，req={}, userId=[{}], sessionKey=[{}], token=[{}]",
                JsonUtility.toJson(soaRequest), userId, sessionKey, token);
        String sessionTicket = AESUtil.encrypt(SessionUtil.getSessionTicket(token), setting.getSessionTokenAesKey(), setting.getSessionTokenAesIV());
        return SessionTicketResponse.builder().sessionTicket(sessionTicket).build();
    }
}
