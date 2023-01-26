package com.upuphone.cloudplatform.usercenter.service.userbasic.login.checker;

import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.ValidCodeUtils;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.checker.ValidCodeCheckerTypeEnum;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.util.NewDeviceLoginUtil;
import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUseValidCodeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Classname SecurityPhoneChecker
 * @Description
 * @Date 2022/3/31 6:27 下午
 * @Created by gz-d
 */
@Component
@Slf4j
public class SecurityPhoneChecker implements NewDeviceUseValideCodeChecker {
    @Autowired
    private CommonService commonService;

    @Autowired
    private ValidCodeUtils validCodeUtils;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private NewDeviceLoginUtil newDeviceLoginUtil;

    @Override
    public void checkValidCode(LoginUseValidCodeRequest request) {
        String phoneNumberWithAreaCode = commonService.getFormattedPhoneFromUserIdAndType(
                newDeviceLoginUtil.getUserIdFromSecret(request.getSecondLoginSecret()), 1);
        validCodeUtils.check(ValidCodeCheckerTypeEnum.SMS, phoneNumberWithAreaCode, RequestContext.getDeviceId(),
                request.getValidCode(), LoginTypeEnum.SECURITY_PHONE_VALIDCODE.getValidCodeType());
    }

    @Override
    public void checkTicket(String redisKey, String ticket) {
        boolean isPhoneValidCodeNewDeviceCheck = (ticket + "-" + LoginTypeEnum.PHONE_VALIDCODE.getType())
                .equals(stringRedisTemplate.opsForValue().get(redisKey));
        boolean isThirdPartNewDeviceCheck = (ticket + "-" + LoginTypeEnum.THIRD_PART_LOGIN.getType())
                .equals(stringRedisTemplate.opsForValue().get(redisKey));
        boolean isFlashLoginNewDeviceCheck = (ticket + "-" + LoginTypeEnum.FLASH_LOGIN.getType())
                .equals(stringRedisTemplate.opsForValue().get(redisKey));
        boolean isPwdLoginNewDeviceCheck = (ticket + "-" + LoginTypeEnum.PWD.getType())
                .equals(stringRedisTemplate.opsForValue().get(redisKey));
        if (isPhoneValidCodeNewDeviceCheck || isThirdPartNewDeviceCheck
                || isFlashLoginNewDeviceCheck || isPwdLoginNewDeviceCheck) {
            log.info("有密钥，且跟redis中该（user+device）的密钥相等");
            stringRedisTemplate.delete(redisKey);
        } else {
            log.info("二次校验凭证错误");
            throw new BusinessException(UserCenterErrorCode.TICKET_ERROR);
        }
    }
}
