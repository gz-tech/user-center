package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.redis.MaskTypeEnum;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.util.UserSecurityUtils;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.MobileAndEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GetMobileAndEmailLoggedInService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserSecurityUtils userSecurityUtils;
    @Autowired
    private CommonService commonService;
    @Autowired
    private Setting setting;

    private void validate() {

    }

    public MobileAndEmailResponse process() {
        validate();
        String maskMobileEmailUserKey = RedisKeyUtils.getMaskMobileEmailUserKey(MaskTypeEnum.USER_ID, RequestContext.getUserId().toString());
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(maskMobileEmailUserKey))) {
            return userSecurityUtils.getMobileAndEmailFromRedis(null, maskMobileEmailUserKey);
        }
        UserBaseInfoPo po = commonService.getUserBaseInfoById(RequestContext.getUserId());
        if (null == po) {
            log.error("[GetMobileAndEmailLoggedInService]用户不存在，userId={}", RequestContext.getUserId());
            stringRedisTemplate.opsForValue().set(maskMobileEmailUserKey, StringUtils.EMPTY, setting.getSessionTokenDuration(), TimeUnit.SECONDS);
            throw new BusinessException(UserCenterErrorCode.USER_NOT_FOUND);
        }
        return userSecurityUtils.getMobileAndEmailResponseFromUserPo(po, maskMobileEmailUserKey);
    }
}
