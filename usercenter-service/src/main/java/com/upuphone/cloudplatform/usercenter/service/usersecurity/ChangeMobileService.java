package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.common.util.MaskUtil;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
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
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ChangeMobileRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.NewMobileResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.AREA_CODE_CHINA;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.MOBILE_EXP;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.MOBILE_EXP_MSG;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.VALID_CODE_MSG;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/27
 */
@Service
@Slf4j
public class ChangeMobileService extends BaseService<ChangeMobileRequest, NewMobileResponse> {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private CommonService commonService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserSecurityUtils userSecurityUtils;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ValidCodeUtils validCodeUtils;
    @Autowired
    private Setting setting;

    @Override
    protected void validate(ChangeMobileRequest request) {
        String validCode = request.getValidCode();
        if (InputValidateUtil.isContainSpace(validCode) || InputValidateUtil.isContainChinese(validCode)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, VALID_CODE_MSG);
        }
        if (AREA_CODE_CHINA.equals(request.getTelephoneCode())
                && !Pattern.matches(MOBILE_EXP, request.getTelePhoneNumber())) {
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, MOBILE_EXP_MSG);
        }
        if (InputValidateUtil.hasNonNumber(request.getTelePhoneNumber())
                || InputValidateUtil.hasNonNumber(request.getTelephoneCode())) {
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, MOBILE_EXP_MSG);
        }
    }

    @Override
    protected NewMobileResponse processCore(ChangeMobileRequest soaRequest) throws Exception {
        String phoneNumber = soaRequest.getTelePhoneNumber();
        String phoneNumberWithAreaCode =
                PhoneUtil.formatPhoneNumber(soaRequest.getTelephoneCode(), phoneNumber);
        String deviceId = RequestContext.getDeviceId();
        validCodeUtils.check(ValidCodeCheckerTypeEnum.SMS, phoneNumberWithAreaCode, deviceId,
                soaRequest.getValidCode(), ValidCodeType.CHANGE_BIND_MOBILE);
        Long userId = RequestContext.getUserId();
        String sessionKey = RedisKeyUtils.getRedisSessionKey(SessionTypeEnum.USER_CHANGE_BIND_MOBILE_SESSION, String.valueOf(userId));
        String sessionToken = stringRedisTemplate.opsForValue().get(sessionKey);
        if (!Objects.equals(AESUtil.decrypt(RequestContext.getSessionTicket(), setting.getSessionTokenAesKey(), setting.getSessionTokenAesIV()),
                SessionUtil.getSessionTicket(sessionToken))) {
            log.error("[ChangeMobileService]修改绑定手机号时session-token已过期或不符, req={}, userId=[{}], key=[{}], token=[{}]",
                    JsonUtility.toJson(soaRequest), userId, sessionKey, sessionToken);
            throw new BusinessException(UserCenterErrorCode.STEP_SESSION_EXPIRED);
        }
        String mobileLockKey = RedisKeys.registLockKey(phoneNumberWithAreaCode);
        RLock lock = redissonClient.getLock(mobileLockKey);
        try {
            if (!lock.tryLock(0, 10, TimeUnit.SECONDS)) {
                throw new BusinessException(UserCenterErrorCode.STEP_SESSION_CONFLICTED);
            }
            // 判断该手机号是否绑定了其他帐号
            if (0 != userBaseInfoMapper.selectCount(new LambdaQueryWrapper<UserBaseInfoPo>()
                    .eq(UserBaseInfoPo::getPhoneNumber, phoneNumber))) {
                log.error("[ChangeMobileService]该手机号已绑定其他帐号，req={}, userId=[{}]",
                        JsonUtility.toJson(soaRequest), userId);
                throw new BusinessException(UserCenterErrorCode.ALREADY_REGISTERED);
            }
            UserBaseInfoPo po = commonService.getUserBaseInfoById(userId);
            po.setPhoneCode(PhoneUtil.formatPhoneAreaCode(soaRequest.getTelephoneCode()));
            po.setPhoneNumber(phoneNumber);
            if (0 == userBaseInfoMapper.updateById(po)) {
                log.error("[ChangeMobileService]修改绑定手机失败，req={}, userId=[{}]",
                        JsonUtility.toJson(soaRequest), userId);
                throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, "修改绑定手机失败");
            }
            log.info("[ChangeMobileService]修改绑定手机成功, req={}, userId=[{}]",
                    JsonUtility.toJson(soaRequest), userId);
            stringRedisTemplate.delete(sessionKey);
            userSecurityUtils.removeMaskMobileEmailUserKey(po);
            commonService.removePhoneCache(userId);
            return NewMobileResponse.builder().newMobile(MaskUtil.maskPhone(phoneNumber)).build();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
