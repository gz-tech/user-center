package com.upuphone.cloudplatform.usercenter.service.userbasic.util;

import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.common.util.DateUtil;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author guangzheng.ding
 * @date 2021/12/27 19:13
 */
@Service
@Slf4j
public class PasswordUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private Setting setting;

    private void handlePasswordErrorMethod(Long userId) {
        String passwordErrorTimesKey = RedisKeys.getPasswordErrorTimesKey(userId.toString());
        Long passwordErrorTimes = stringRedisTemplate.opsForValue().increment(passwordErrorTimesKey);
        if (passwordErrorTimes == 1) {
            log.info("第一次密码错误，设置redis过期时间为0点");
            stringRedisTemplate.expire(passwordErrorTimesKey, DateUtil.getSecondsNextEarlyMorning(), TimeUnit.SECONDS);
        }
        if (passwordErrorTimes < 4) {
            throw new BusinessException(UserCenterErrorCode.PASSWORD_ERROR);
        } else if (passwordErrorTimes < setting.getPasswordErrorMaxTimes()) {
            throw new BusinessException(UserCenterErrorCode.PASSWORD_ERROR_HINT);
        } else {
            log.info("今日用户{}密码错误次数大于{}", userId, setting.getPasswordErrorMaxTimes());
            stringRedisTemplate.opsForValue().set(RedisKeys.getAccountBlockKey(userId.toString()), "block",
                    setting.getPasswordErrorBlockHours(), TimeUnit.HOURS);
            stringRedisTemplate.expire(passwordErrorTimesKey, setting.getPasswordErrorBlockHours(), TimeUnit.HOURS);
            throw new BusinessException(UserCenterErrorCode.ACCOUNT_BLOCKED);
        }
    }

    public void checkPassword(Long userId, String textPassword, String hashedPassword) {
        if (stringRedisTemplate.opsForValue().get(RedisKeys.getAccountBlockKey(userId.toString())) != null) {
            log.info("密码错误次数过多，帐号被锁定");
            throw new BusinessException(UserCenterErrorCode.ACCOUNT_BLOCKED);
        }
        if (!BCrypt.checkpw(textPassword, hashedPassword)) {
            handlePasswordErrorMethod(userId);
        } else {
            stringRedisTemplate.delete(RedisKeys.getPasswordErrorTimesKey(userId.toString()));
        }
    }
}
