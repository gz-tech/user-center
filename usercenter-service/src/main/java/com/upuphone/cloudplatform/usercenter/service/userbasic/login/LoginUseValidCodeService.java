package com.upuphone.cloudplatform.usercenter.service.userbasic.login;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.handler.LoginValidCodeHandler;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.handler.NewDeviceValidCodeLoginHandler;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.handler.PhoneValidCodeLoginHandler;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.util.ThirdPartAccountUtil;
import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUseValidCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.upuphone.cloudplatform.usercenter.service.util.InputValidateUtil.NUMBER;

/**
 * @author guangzheng.ding
 * @date 2021/12/19 13:33
 */
@Service
@Slf4j
public class LoginUseValidCodeService extends BaseService<LoginUseValidCodeRequest, LoginResponse> {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private PhoneValidCodeLoginHandler phoneValidCodeLoginHandler;

    @Autowired
    private NewDeviceValidCodeLoginHandler newDeviceValidCodeLoginHandler;
    @Autowired
    private LoginCommonService loginCommonService;
    @Autowired
    private ThirdPartAccountUtil thirdPartAccountUtil;

    @Override
    protected void validate(LoginUseValidCodeRequest request) {
        loginCommonService.loginHeaderCheck();
        if (StringUtils.isEmpty(request.getSecondLoginSecret()) && StringUtils.isEmpty(request.getPhoneNumber())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "帐号不为空");
        }
        if (StringUtil.isEmpty(request.getSecondLoginSecret())
                && !Pattern.compile(NUMBER).matcher(request.getPhoneNumber()).matches()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "手机号只包含数字");
        }
        if (!Pattern.compile(NUMBER).matcher(request.getValidCode()).matches()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "验证码只包含数字,且至少四位");
        }
    }

    @Override
    protected LoginResponse processCore(LoginUseValidCodeRequest request) throws Exception {
        LoginValidCodeHandler handler =
                StringUtils.isEmpty(request.getSecondLoginSecret()) ? phoneValidCodeLoginHandler : newDeviceValidCodeLoginHandler;
        UserBaseInfoPo user = handler.getUser(request);
        String lockKey = RedisKeyUtils.getIdempotentCheckKey(LoginTypeEnum.PHONE_VALIDCODE, user.getId(), RequestContext.getDeviceId());
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(0, 30, TimeUnit.SECONDS)) {
                throw new BusinessException(CommonErrorCode.OPERATION_ERROR, "please not repeat request");
            }
            thirdPartAccountUtil.loginCheckThirdPartBinded(request.getThirdPartyBindTicket(), user.getId());
            LoginResponse result = handler.handleLogin(user, request);
            if (StringUtils.isEmpty(result.getSecondLoginSecret())
                    && !StringUtils.isEmpty(request.getThirdPartyBindTicket())) {
                log.info("登录成功，绑定三方帐号");
                loginCommonService.bindThirdPartyAccount(request.getThirdPartyBindTicket(), user.getId(), false);
            }
            return result;
        } finally {
            log.info("用户{}的设备{}结束登录", user.getId(), RequestContext.getDeviceId());
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
