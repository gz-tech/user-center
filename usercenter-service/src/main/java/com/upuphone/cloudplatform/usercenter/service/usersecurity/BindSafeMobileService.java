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
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.redis.SessionTypeEnum;
import com.upuphone.cloudplatform.usercenter.service.common.CommonService;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.ValidCodeUtils;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.checker.ValidCodeCheckerTypeEnum;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.util.UserSecurityUtils;
import com.upuphone.cloudplatform.usercenter.service.util.InputValidateUtil;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.BindSafeMobileRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.NewMobileResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.AREA_CODE_CHINA;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.MOBILE_EXP;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.MOBILE_EXP_MSG;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.VALID_CODE_MSG;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/24
 */
@Slf4j
@Service
public class BindSafeMobileService extends BaseService<BindSafeMobileRequest, NewMobileResponse> {

    @Autowired
    private CommonService commonService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserSecurityUtils userSecurityUtils;
    @Autowired
    private ValidCodeUtils validCodeUtils;
    @Autowired
    private Setting setting;

    @Override
    protected void validate(BindSafeMobileRequest bindSafeMobileRequest) {
        String validCode = bindSafeMobileRequest.getValidCode();
        if (InputValidateUtil.isContainSpace(validCode) || InputValidateUtil.isContainChinese(validCode)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, VALID_CODE_MSG);
        }
        if (AREA_CODE_CHINA.equals(bindSafeMobileRequest.getTelephoneCode())
                && !Pattern.matches(MOBILE_EXP, bindSafeMobileRequest.getTelePhoneNumber())) {
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, MOBILE_EXP_MSG);
        }
        if (InputValidateUtil.hasNonNumber(bindSafeMobileRequest.getTelePhoneNumber())
                || InputValidateUtil.hasNonNumber(bindSafeMobileRequest.getTelephoneCode())) {
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, MOBILE_EXP_MSG);
        }
    }

    @Override
    protected NewMobileResponse processCore(BindSafeMobileRequest bindSafeMobileRequest) throws Exception {
        String safeMobileWithAreaCode =
                PhoneUtil.formatPhoneNumber(bindSafeMobileRequest.getTelephoneCode(), bindSafeMobileRequest.getTelePhoneNumber());
        String deviceId = RequestContext.getDeviceId();
        validCodeUtils.check(ValidCodeCheckerTypeEnum.SMS, safeMobileWithAreaCode, deviceId, bindSafeMobileRequest.getValidCode(),
                ValidCodeType.CHANGE_SECURITY_MOBILE);
        Long userId = RequestContext.getUserId();
        UserBaseInfoPo po = commonService.getUserBaseInfoById(userId);
        String sessionKey = RedisKeyUtils.getRedisSessionKey(SessionTypeEnum.USER_BIND_SECURITY_MOBILE_SESSION, String.valueOf(userId));
        String sessionToken = stringRedisTemplate.opsForValue().get(sessionKey);
        if (!Objects.equals(AESUtil.decrypt(RequestContext.getSessionTicket(), setting.getSessionTokenAesKey(), setting.getSessionTokenAesIV()),
                SessionUtil.getSessionTicket(sessionToken))) {
            log.error("[BindSafeMobileService]????????????????????????session-token??????????????????, req={}, userId=[{}], key=[{}], token=[{}]",
                    JsonUtility.toJson(bindSafeMobileRequest), userId, sessionKey, sessionToken);
            throw new BusinessException(UserCenterErrorCode.STEP_SESSION_EXPIRED);
        }
        if (StringUtils.isNotBlank(po.getSecurityPhoneNumber())) {
            log.error("[BindSafeMobileService]?????????????????????????????????????????????????????????????????????, req={}",
                    JsonUtility.toJson(bindSafeMobileRequest));
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, "?????????????????????????????????????????????????????????????????????");
        }
        String safeMobile = bindSafeMobileRequest.getTelePhoneNumber();
        po.setSecurityPhoneCode(PhoneUtil.formatPhoneAreaCode(bindSafeMobileRequest.getTelephoneCode()));
        po.setSecurityPhoneNumber(safeMobile);
        if (!po.updateById()) {
            log.error("[BindSafeMobileService]???????????????????????????req={}",
                    JsonUtility.toJson(bindSafeMobileRequest));
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, "????????????????????????");
        }
        log.info("[BindSafeMobileService]?????????????????????????????????userId=[{}]", userId);
        stringRedisTemplate.delete(sessionKey);
        userSecurityUtils.removeMaskMobileEmailUserKey(po);
        commonService.removePhoneCache(userId);
        return NewMobileResponse.builder().newMobile(MaskUtil.maskPhone(safeMobile)).build();
    }
}
