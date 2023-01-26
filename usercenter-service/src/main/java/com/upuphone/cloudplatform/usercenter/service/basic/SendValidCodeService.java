package com.upuphone.cloudplatform.usercenter.service.basic;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.constants.CaptchaBusinessEnum;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.remote.sms.model.SmsType;
import com.upuphone.cloudplatform.usercenter.service.captcha.entity.CaptchaValidateBo;
import com.upuphone.cloudplatform.usercenter.service.captcha.utils.CaptchaUtil;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.ValidCodeUtils;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.validate.ChangeBindMobileStrategy;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.validate.PhoneValidCodeLoginStrategy;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.validate.PhoneValidCodeRegistStrategy;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.repo.AccountBindingRepo;
import com.upuphone.cloudplatform.usercenter.service.util.InputValidateUtil;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.SendValidCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.SendValidCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.AREA_CODE_CHINA;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.MOBILE_EXP;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.MOBILE_EXP_MSG;

@Slf4j
@Component
public class SendValidCodeService extends BaseService<SendValidCodeRequest, SendValidCodeResponse> {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ValidCodeUtils validCodeUtils;

    @Autowired
    private ChangeBindMobileStrategy changeBindMobileStrategy;

    @Autowired
    private PhoneValidCodeLoginStrategy phoneValidCodeLoginStrategy;

    @Autowired
    private PhoneValidCodeRegistStrategy phoneValidCodeRegistStrategy;
    @Autowired
    private AccountBindingRepo accountBindingRepo;
    @Autowired
    private CaptchaUtil captchaUtil;
    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private Setting setting;

    @Override
    protected void validate(SendValidCodeRequest request) {
        if (AREA_CODE_CHINA.equals(request.getPhoneCode())
                && !Pattern.matches(MOBILE_EXP, request.getPhoneNumber())) {
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, MOBILE_EXP_MSG);
        }
        if (InputValidateUtil.hasNonNumber(request.getPhoneCode())
                || InputValidateUtil.hasNonNumber(request.getPhoneNumber())) {
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, MOBILE_EXP_MSG);
        }
        if (SmsType.isInvalidType(request.getType().getType())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "发送类型不合法");
        }
        if (ValidCodeType.isInvalidTypeForPhoneNumber(request.getType())) {
            throw new BusinessException(UserCenterErrorCode.VALIDCODE_CHECK_NOT_SUPPORTED);
        }
    }


    @Override
    protected SendValidCodeResponse processCore(SendValidCodeRequest sendValidCodeRequest) throws Exception {
        // 检证是否满足最小发送间隔
        String redisKey = validCodeUtils.getMobileValidCodeRedisKey(sendValidCodeRequest);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            log.error("距离上次发送验证码间隔过短，请稍后再试, req={}", JsonUtility.toJson(sendValidCodeRequest));
            throw new BusinessException(UserCenterErrorCode.VALIDCODE_SEND_GAP_TOO_SHORT);
        }

        // 提前验证手机是否被绑定
        if (sendValidCodeRequest.getType() == ValidCodeType.CHANGE_BIND_MOBILE) {
            changeBindMobileStrategy.validate(sendValidCodeRequest);
        }
        if (Objects.equals(sendValidCodeRequest.getType(), ValidCodeType.LOGIN)) {
            phoneValidCodeLoginStrategy.validate(sendValidCodeRequest);
        }

        CaptchaValidateBo captchaValidateBo = new CaptchaValidateBo();
        captchaValidateBo.setValidate(sendValidCodeRequest.getValidate());
        String formatNumber = PhoneUtil.formatPhoneNumber(sendValidCodeRequest.getPhoneCode(), sendValidCodeRequest.getPhoneNumber());
        captchaValidateBo.setUniqueId(formatNumber);
        if (Objects.equals(sendValidCodeRequest.getType(), ValidCodeType.REGISTER)) {
            captchaValidateBo.setCaptchaBusinessType(CaptchaBusinessEnum.REGIESTER_MODULE);
            //滑块安全验证
            captchaUtil.captchaValidate(captchaValidateBo);
            //验证是否已注册
            phoneValidCodeRegistStrategy.validate(sendValidCodeRequest);
        }
        if (Objects.equals(sendValidCodeRequest.getType(), ValidCodeType.REGISTER_LOGIN_COMBINE)) {
            String phoneCode = PhoneUtil.formatPhoneAreaCode(sendValidCodeRequest.getPhoneCode());
            String phoneNumber = sendValidCodeRequest.getPhoneNumber();
            Long count = userBaseInfoMapper.selectCount(Wrappers.<UserBaseInfoPo>lambdaQuery()
                    .eq(UserBaseInfoPo::getPhoneCode, phoneCode)
                    .eq(UserBaseInfoPo::getPhoneNumber, phoneNumber));
            if (count < 1 && Objects.equals(RequestContext.getAppId(), setting.getAccountAppId())) {
                log.info("简易流程，未注册先滑块验证");
                captchaValidateBo.setCaptchaBusinessType(CaptchaBusinessEnum.REGIESTER_MODULE);
                //滑块安全验证
                captchaUtil.captchaValidate(captchaValidateBo);
            }
        }
        if (null != sendValidCodeRequest.getThirdBindType()) {
            accountBindingRepo.checkIfBindedBeforeSendValidCode(sendValidCodeRequest.getPhoneNumber(), sendValidCodeRequest.getThirdBindType());
        }
        validCodeUtils.sendSmsRemote(sendValidCodeRequest);
        SendValidCodeResponse result = new SendValidCodeResponse();
        result.setTelePhoneNumber(sendValidCodeRequest.getPhoneNumber());
        return result;
    }
}
