package com.upuphone.cloudplatform.usercenter.service.userbasic.login.handler;

import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.TokenService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.checker.NewDeviceUseEmailValidCodeChecker;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.checker.NewDeviceUseValideCodeChecker;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.checker.PhoneValidCodeChecker;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.checker.SecurityPhoneChecker;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.util.NewDeviceLoginUtil;
import com.upuphone.cloudplatform.usercenter.vo.SimpleUserInfoVo;
import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUseValidCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewDeviceValidCodeLoginHandler implements LoginValidCodeHandler {
    @Autowired
    private NewDeviceLoginUtil newDeviceLoginUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PhoneValidCodeChecker phoneValidCodeChecker;

    @Autowired
    private NewDeviceUseEmailValidCodeChecker newDeviceEmailValidCodeChecker;

    @Autowired
    private SecurityPhoneChecker securityPhoneChecker;


    @Override
    public UserBaseInfoPo getUser(LoginUseValidCodeRequest request) {
        return newDeviceLoginUtil.getUser(newDeviceLoginUtil.getUserIdFromSecret(request.getSecondLoginSecret()));
    }

    @Override
    public LoginResponse handleLogin(UserBaseInfoPo user, LoginUseValidCodeRequest request) {
        NewDeviceUseValideCodeChecker checker = getChecker(request.getLoginType());
        checker.checkValidCode(request);
        checker.checkTicket(RedisKeys.getUserSecondLoginKey(newDeviceLoginUtil.getUserIdFromSecret(request.getSecondLoginSecret()),
                RequestContext.getDeviceId()), request.getSecondLoginSecret());
        List<String> tokens = tokenService.tokenPersistence(user.getId());
        if (CollectionUtils.isNotEmpty(tokens)) {
            stringRedisTemplate.delete(RedisKeys.getUserSecondLoginKey(
                    newDeviceLoginUtil.getUserIdFromSecret(request.getSecondLoginSecret()), RequestContext.getDeviceId()));
        }
        return new LoginResponse(tokens.get(0), tokens.get(1),
                new SimpleUserInfoVo(user.getId(), user.getUserName(), user.getPhotoUrl()));
    }

    private NewDeviceUseValideCodeChecker getChecker(LoginTypeEnum loginType) {
        switch (loginType) {
            case NEWDEVICE_EMAIL:
                return newDeviceEmailValidCodeChecker;
            case NEWDEVICE_VALIDCODE:
                return phoneValidCodeChecker;
            case SECURITY_PHONE_VALIDCODE:
                return securityPhoneChecker;
            default:
                throw new BusinessException(UserCenterErrorCode.VALIDCODE_CHECK_NOT_SUPPORTED);
        }
    }
}

