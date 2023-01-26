package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
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
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ChangeSafeMobileRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.NewMobileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.AREA_CODE_CHINA;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.MOBILE_EXP;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.MOBILE_EXP_MSG;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/23
 */
@Service
@Slf4j
public class ChangeSafeMobileService extends BaseService<ChangeSafeMobileRequest, NewMobileResponse> {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CommonService commonService;
    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private UserSecurityUtils userSecurityUtils;
    @Autowired
    private ValidCodeUtils validCodeUtils;
    @Autowired
    private Setting setting;

    @Override
    protected void validate(ChangeSafeMobileRequest changeSafeMobileRequest) {
        if (AREA_CODE_CHINA.equals(changeSafeMobileRequest.getTelephoneCode())
                && !Pattern.matches(MOBILE_EXP, changeSafeMobileRequest.getTelePhoneNumber())) {
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, MOBILE_EXP_MSG);
        }
        if (InputValidateUtil.hasNonNumber(changeSafeMobileRequest.getTelephoneCode())
                || InputValidateUtil.hasNonNumber(changeSafeMobileRequest.getTelePhoneNumber())) {
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, MOBILE_EXP_MSG);
        }
    }

    @Override
    protected NewMobileResponse processCore(ChangeSafeMobileRequest changeSafeMobileRequest) throws Exception {
        String newMobile = changeSafeMobileRequest.getTelePhoneNumber();
        String newMobileWithAreaCode =
                PhoneUtil.formatPhoneNumber(changeSafeMobileRequest.getTelephoneCode(), newMobile);
        String deviceId = RequestContext.getDeviceId();
        // 校验设定的新安全手机号验证码
        validCodeUtils.check(ValidCodeCheckerTypeEnum.SMS, newMobileWithAreaCode, deviceId, changeSafeMobileRequest.getValidCode(),
                ValidCodeType.CHANGE_SECURITY_MOBILE);
        Long userId = RequestContext.getUserId();
        String sessionKey = RedisKeyUtils.getRedisSessionKey(SessionTypeEnum.USER_CHANGE_SECURITY_MOBILE_SESSION, String.valueOf(userId));
        String sessionToken = stringRedisTemplate.opsForValue().get(sessionKey);
        if (!Objects.equals(AESUtil.decrypt(RequestContext.getSessionTicket(), setting.getSessionTokenAesKey(), setting.getSessionTokenAesIV()),
                SessionUtil.getSessionTicket(sessionToken))) {
            log.error("[ChangeSafeMobileService]修改绑定手机号时session-token已过期或不符, req={}, userId=[{}], key=[{}], token=[{}]",
                    JsonUtility.toJson(changeSafeMobileRequest), userId, sessionKey, sessionToken);
            throw new BusinessException(UserCenterErrorCode.STEP_SESSION_EXPIRED);
        }
        UserBaseInfoPo po = commonService.getUserBaseInfoById(userId);
        String newMobileCode = PhoneUtil.formatPhoneAreaCode(changeSafeMobileRequest.getTelephoneCode());
        if (!Objects.equals(po.getSecurityPhoneCode(), newMobileCode)
                || !Objects.equals(po.getSecurityPhoneNumber(), newMobile)) {
            po.setSecurityPhoneCode(newMobileCode);
            po.setSecurityPhoneNumber(newMobile);
            if (0 == userBaseInfoMapper.updateById(po)) {
                log.error("[ChangeSafeMobileService]修改安全手机失败, userId=[{}], request={}",
                        userId, JsonUtility.toJson(changeSafeMobileRequest));
                throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, "修改安全手机失败");
            }
        }
        // 删除session
        stringRedisTemplate.delete(sessionKey);
        userSecurityUtils.removeMaskMobileEmailUserKey(po);
        commonService.removePhoneCache(userId);
        return NewMobileResponse.builder().newMobile(MaskUtil.maskPhone(newMobile)).build();
    }
}
