package com.upuphone.cloudplatform.usercenter.service.userbasic.login.handler;

import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.TokenService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.util.NewDeviceLoginUtil;
import com.upuphone.cloudplatform.usercenter.vo.SimpleUserInfoVo;
import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUsePasswordRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname NewDeviceLoginUsePwdHandler
 * @Description
 * @Date 2022/4/1 5:53 下午
 * @Created by gz-d
 */
@Component
@Slf4j
public class NewDeviceLoginUsePwdHandler implements LoginPwdHandler {

    @Autowired
    private NewDeviceLoginUtil newDeviceLoginUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TokenService tokenService;

    @Override
    public UserBaseInfoPo getUser(LoginUsePasswordRequest request) {
        return newDeviceLoginUtil.getUser(newDeviceLoginUtil.getUserIdFromSecret(request.getSecondLoginSecret()));
    }

    @Override
    public LoginResponse handleLogin(UserBaseInfoPo user, String secondLoginSecret) {
        checkTicket(RedisKeys.getUserSecondLoginKey(user.getId(), RequestContext.getDeviceId()), secondLoginSecret);
        List<String> tokens = tokenService.tokenPersistence(user.getId());
        if (CollectionUtils.isNotEmpty(tokens)) {
            stringRedisTemplate.delete(RedisKeys.getUserSecondLoginKey(user.getId(), RequestContext.getDeviceId()));
        }
        return new LoginResponse(tokens.get(0), tokens.get(1),
                new SimpleUserInfoVo(user.getId(), user.getUserName(), user.getPhotoUrl()));
    }

    private void checkTicket(String redisKey, String ticket) {
        boolean isPhoneValidCodeNewDeviceCheck = (ticket + "-" + LoginTypeEnum.PHONE_VALIDCODE.getType())
                .equals(stringRedisTemplate.opsForValue().get(redisKey));
        boolean isThirdPartNewDeviceCheck = (ticket + "-" + LoginTypeEnum.THIRD_PART_LOGIN.getType())
                .equals(stringRedisTemplate.opsForValue().get(redisKey));
        boolean isFlashLoginNewDeviceCheck = (ticket + "-" + LoginTypeEnum.FLASH_LOGIN.getType())
                .equals(stringRedisTemplate.opsForValue().get(redisKey));
        //交叉验证，验证码、微信登录、一键登录可以使用密码登录校验。
        if (isPhoneValidCodeNewDeviceCheck || isThirdPartNewDeviceCheck || isFlashLoginNewDeviceCheck) {
            log.info("有密钥，且跟redis中该（user+device）的密钥相等");
            stringRedisTemplate.delete(redisKey);
        } else {
            log.info("二次校验凭证错误");
            throw new BusinessException(UserCenterErrorCode.TICKET_ERROR);
        }
    }
}
