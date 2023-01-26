package com.upuphone.cloudplatform.usercenter.service.userbasic.login;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.constants.CaptchaBusinessEnum;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.remote.flashlogin.FlashLoginMobileQueryRemoteService;
import com.upuphone.cloudplatform.usercenter.remote.flashlogin.model.FlashLoginMobileQueryRequest;
import com.upuphone.cloudplatform.usercenter.remote.flashlogin.model.FlashLoginMobileQueryResponse;
import com.upuphone.cloudplatform.usercenter.service.captcha.entity.CaptchaValidateBo;
import com.upuphone.cloudplatform.usercenter.service.captcha.utils.CaptchaUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.util.TicketUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.util.ThirdPartAccountUtil;
import com.upuphone.cloudplatform.usercenter.setting.RegisterSetting;
import com.upuphone.cloudplatform.usercenter.vo.request.login.FlashLoginRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.SIMPLE_LOGIN;

/**
 * @Classname FlashLoginService
 * @Description
 * @Date 2022/1/24 8:01 下午
 * @Created by gz-d
 */
@Service
@Slf4j
@SuppressWarnings("unchecked")
public class FlashLoginService extends BaseService<FlashLoginRequest, LoginResponse> {

    @Autowired
    private FlashLoginMobileQueryRemoteService flashLoginMobileQueryRemoteService;

    @Autowired
    private LoginCommonService loginCommonService;

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Autowired
    private TicketUtil ticketUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RegisterSetting registerSetting;
    @Autowired
    private ThirdPartAccountUtil thirdPartAccountUtil;
    @Autowired
    private CaptchaUtil captchaUtil;

    @Override
    protected void validate(FlashLoginRequest request) {
        loginCommonService.loginHeaderCheck();
    }

    @Override
    protected LoginResponse processCore(FlashLoginRequest request) throws Exception {
        String token = request.getToken();
        FlashLoginMobileQueryRequest queryRequest = new FlashLoginMobileQueryRequest();
        queryRequest.setToken(token);
        FlashLoginMobileQueryResponse queryResponse = flashLoginMobileQueryRemoteService.process(queryRequest);
        if (null == queryResponse) {
            throw new BusinessException(CommonErrorCode.SERVICE_ERROR, "获取mobile为空");
        }
        String mobile = queryResponse.getMobile();
        UserBaseInfoPo userBaseInfoPo = userBaseInfoMapper.selectOne(Wrappers.<UserBaseInfoPo>lambdaQuery()
                .eq(UserBaseInfoPo::getPhoneNumber, mobile));
        LoginResponse result = new LoginResponse();
        if (null != userBaseInfoPo) {
            thirdPartAccountUtil.loginCheckThirdPartBinded(request.getThirdPartyBindTicket(), userBaseInfoPo.getId());
            result = loginCommonService.doLogin(userBaseInfoPo, LoginTypeEnum.FLASH_LOGIN.getType());
            result.setErrorCode(CommonErrorCode.SUCCESS);
            if (Strings.isBlank(result.getSecondLoginSecret())
                    && Strings.isNotBlank(request.getThirdPartyBindTicket())) {
                log.info("登录成功,且需要绑定三方");
                loginCommonService.bindThirdPartyAccount(request.getThirdPartyBindTicket(), userBaseInfoPo.getId(), false);
            }
        } else {
            if (SIMPLE_LOGIN.equals(request.getLoginMode())) {
                CaptchaValidateBo captchaValidateBo = new CaptchaValidateBo();
                captchaValidateBo.setValidate(request.getValidate());
                String phoneNumberWithAreaCode = PhoneUtil.formatPhoneNumber("0086", mobile);
                captchaValidateBo.setUniqueId(phoneNumberWithAreaCode);
                captchaValidateBo.setCaptchaBusinessType(CaptchaBusinessEnum.REGIESTER_MODULE);
                captchaUtil.captchaValidate(captchaValidateBo);

                String ticket = ticketUtil.generateTicket(RequestContext.getDeviceId(), mobile, null, null);
                result.setErrorCode(CommonErrorCode.SUCCESS);
                //TODO phoneNumberWithAreaCode
                String ticketKey = RedisKeys.registTicket(phoneNumberWithAreaCode);
                stringRedisTemplate.opsForValue().set(ticketKey, ticket,
                        registerSetting.getRegistTicketValidDuration(), TimeUnit.SECONDS);
                result.setTicket(ticket);
                result.setPhoneNumber(mobile);
                return result;
            } else {
                log.info("未注册用户[{}]", mobile);
                result.setPhoneNumber(mobile);
                result.setErrorCode(UserCenterErrorCode.NOT_REGISTERED_ERROR);
            }
        }
        return result;
    }
}
