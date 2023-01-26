package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.common.util.MaskUtil;
import com.upuphone.cloudplatform.usercenter.common.util.SessionUtil;
import com.upuphone.cloudplatform.usercenter.common.util.encryption.AESUtil;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.redis.SessionTypeEnum;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.ValidCodeUtils;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.checker.ValidCodeCheckerTypeEnum;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.util.UserSecurityUtils;
import com.upuphone.cloudplatform.usercenter.service.util.InputValidateUtil;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ChangeEmailRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.NewEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.VALID_CODE_MSG;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/8
 */
@Service
@Slf4j
public class ChangeEmailService extends BaseService<ChangeEmailRequest, NewEmailResponse> {

    @Autowired
    private UserSecurityUtils userSecurityUtils;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private CommonService commonService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ValidCodeUtils validCodeUtils;
    @Autowired
    private Setting setting;

    @Override
    protected void validate(ChangeEmailRequest request) {
        String validCode = request.getValidCode();
        if (InputValidateUtil.isContainSpace(validCode) || InputValidateUtil.isContainChinese(validCode)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, VALID_CODE_MSG);
        }
    }

    @Override
    protected NewEmailResponse processCore(ChangeEmailRequest request) throws Exception {
        Long userId = RequestContext.getUserId();
        String newEmail = request.getNewEmail();
        String deviceId = RequestContext.getDeviceId();
        validCodeUtils.check(ValidCodeCheckerTypeEnum.EMAIL, newEmail, deviceId, request.getValidCode(), ValidCodeType.CHANGE_EMAIL);
        String sessionKey = RedisKeyUtils.getRedisSessionKey(SessionTypeEnum.USER_CHANGE_EMAIL_SESSION, userId.toString());
        String sessionToken = stringRedisTemplate.opsForValue().get(sessionKey);
        if (!Objects.equals(AESUtil.decrypt(RequestContext.getSessionTicket(), setting.getSessionTokenAesKey(), setting.getSessionTokenAesIV()),
                SessionUtil.getSessionTicket(sessionToken))) {
            log.error("[ChangeEmailService]修改绑定邮箱时session-token已过期或不符, req={}, userId=[{}], key=[{}], token=[{}]",
                    JsonUtility.toJson(request), userId, sessionKey, sessionToken);
            throw new BusinessException(UserCenterErrorCode.STEP_SESSION_EXPIRED);
        }
        String emailLockKey = RedisKeys.registLockKey(newEmail);
        RLock lock = redissonClient.getLock(emailLockKey);
        try {
            if (!lock.tryLock(0, 10, TimeUnit.SECONDS)) {
                throw new BusinessException(UserCenterErrorCode.STEP_SESSION_CONFLICTED);
            }
            // 判断邮箱是否绑定了其它帐号
            if (0 != userBaseInfoMapper.selectCount(new LambdaQueryWrapper<UserBaseInfoPo>()
                    .eq(UserBaseInfoPo::getEmail, newEmail))) {
                log.error("[ChangeEmailService]该邮箱已绑定其他帐号，req={}, userId=[{}]",
                        JsonUtility.toJson(request), userId);
                throw new BusinessException(UserCenterErrorCode.ALREADY_REGISTERED);
            }
            UserBaseInfoPo po = commonService.getUserBaseInfoById(userId);
            po.setEmail(newEmail);
            if (0 == userBaseInfoMapper.updateById(po)) {
                log.error("[ChangeEmailService]修改绑定邮箱失败，req={}, userId=[{}]",
                        JsonUtility.toJson(request), userId);
                throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, "修改绑定邮箱失败");
            }
            log.info("[ChangeEmailService]修改绑定邮箱成功, req={}, userId=[{}]",
                    JsonUtility.toJson(request), userId);
            stringRedisTemplate.delete(sessionKey);
            userSecurityUtils.removeMaskMobileEmailUserKey(po);
            commonService.removeEmailCache(userId);
            return NewEmailResponse.builder().newEmail(MaskUtil.maskEmail(newEmail)).build();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
