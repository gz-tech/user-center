package com.upuphone.cloudplatform.usercenter.service.userbasic.login;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.handler.LoginPwdHandler;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.handler.NewDeviceLoginUsePwdHandler;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.handler.NormalLoginUsePwdHandler;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.util.ThirdPartAccountUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.util.PasswordUtil;
import com.upuphone.cloudplatform.usercenter.service.util.InputValidateUtil;
import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUsePasswordRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Classname LoginUserPasswordService
 * @Description
 * @Date 2022/3/31 10:27 上午
 * @Created by gz-d
 */
@Service
@Slf4j
public class LoginUsePasswordService extends BaseService<LoginUsePasswordRequest, LoginResponse> {

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private NormalLoginUsePwdHandler normalLoginUsePwdHandler;

    @Autowired
    private NewDeviceLoginUsePwdHandler newDeviceLoginUsePwdHandler;

    @Autowired
    private LoginCommonService loginCommonService;
    @Autowired
    private ThirdPartAccountUtil thirdPartAccountUtil;

    @Autowired
    private RedissonClient redissonClient;
    @Override
    protected void validate(LoginUsePasswordRequest request) {
        if (StringUtils.isEmpty(request.getSecondLoginSecret()) && StringUtils.isEmpty(request.getAccount())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "帐号不为空");
        }
        loginCommonService.loginHeaderCheck();
        if (InputValidateUtil.isContainChinese(request.getPassword()) ||
                InputValidateUtil.isContainSpace(request.getPassword())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "密码不能包含空格或中文");
        }
    }

    @Override
    protected LoginResponse processCore(LoginUsePasswordRequest request) throws Exception {
        LoginPwdHandler loginPwdHandler =
                StringUtils.isEmpty(request.getSecondLoginSecret()) ? normalLoginUsePwdHandler : newDeviceLoginUsePwdHandler;
        UserBaseInfoPo user = loginPwdHandler.getUser(request);
        String lockKey = RedisKeyUtils.getIdempotentCheckKey(LoginTypeEnum.PWD, user.getId(), RequestContext.getDeviceId());
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(0, 30, TimeUnit.SECONDS)) {
                throw new BusinessException(CommonErrorCode.OPERATION_ERROR, "please not repeat request");
            }
            thirdPartAccountUtil.loginCheckThirdPartBinded(request.getThirdPartyBindTicket(), user.getId());
            passwordUtil.checkPassword(user.getId(), request.getPassword(), user.getPassword());
            LoginResponse response = loginPwdHandler.handleLogin(user, request.getSecondLoginSecret());
            if (StringUtils.isEmpty(response.getSecondLoginSecret())
                    && !StringUtils.isEmpty(request.getThirdPartyBindTicket())) {
                log.info("登录成功，绑定三方帐号");
                loginCommonService.bindThirdPartyAccount(request.getThirdPartyBindTicket(), user.getId(), false);
            }
            return response;
        } finally {
            log.info("用户{}的设备{}结束登录", user.getId(), RequestContext.getDeviceId());
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
