package com.upuphone.cloudplatform.usercenter.service.userbasic.register;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.ValidCodeUtils;
import com.upuphone.cloudplatform.usercenter.service.common.validcode.checker.ValidCodeCheckerTypeEnum;
import com.upuphone.cloudplatform.usercenter.service.userbasic.TokenService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.LoginCommonService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.util.RandomUserNameUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.util.TicketUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.util.ThirdPartAccountUtil;
import com.upuphone.cloudplatform.usercenter.setting.RegisterSetting;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.SimpleUserInfoVo;
import com.upuphone.cloudplatform.usercenter.vo.request.register.SimpleRegisterLoginRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SimpleRegisterLoginService extends BaseService<SimpleRegisterLoginRequest, LoginResponse> {

    @Autowired
    private ValidCodeUtils validCodeUtils;

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Autowired
    private TicketUtil ticketUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RegisterSetting registerSetting;
    @Autowired
    private LoginCommonService loginCommonService;
    @Autowired
    private ThirdPartAccountUtil thirdPartAccountUtil;
    @Autowired
    private Setting setting;
    @Autowired
    private TokenService tokenService;

    @Override
    protected void validate(SimpleRegisterLoginRequest request) {

    }

    @Override
    protected LoginResponse processCore(SimpleRegisterLoginRequest request) throws Exception {
        String phoneNumberWithAreaCode = PhoneUtil.formatPhoneNumber(request.getPhoneCode(), request.getPhoneNumber());
        validCodeUtils.check(ValidCodeCheckerTypeEnum.SMS, phoneNumberWithAreaCode, RequestContext.getDeviceId(),
                request.getValidCode(), ValidCodeType.REGISTER_LOGIN_COMBINE);
        QueryWrapper<UserBaseInfoPo> userQueryMapper = new QueryWrapper<>();
        userQueryMapper.eq("phone_number", request.getPhoneNumber());
        userQueryMapper.eq("phone_code", PhoneUtil.formatPhoneAreaCode(request.getPhoneCode()));
        UserBaseInfoPo userBaseInfoPo = userBaseInfoMapper.selectOne(userQueryMapper);

        LoginResponse result = new LoginResponse();

        if (null == userBaseInfoPo) {
            if (Objects.equals(RequestContext.getAppId(), setting.getAccountAppId())) {
                String redisKey = RedisKeyUtils.getValidCodeKey(ValidCodeType.REGISTER_LOGIN_COMBINE,
                        phoneNumberWithAreaCode, RequestContext.getDeviceId());
                String validCodeInRedis = stringRedisTemplate.opsForValue().get(redisKey);
                String ticket = ticketUtil.generateTicket(RequestContext.getDeviceId(), phoneNumberWithAreaCode, null, validCodeInRedis);
                String ticketKey = RedisKeys.registTicket(phoneNumberWithAreaCode);
                stringRedisTemplate.opsForValue().set(ticketKey, ticket);
                stringRedisTemplate.expire(ticketKey, registerSetting.getRegistTicketValidDuration(), TimeUnit.SECONDS);
                result.setTicket(ticket);
                return result;
            } else {
                UserBaseInfoPo newUser = this.addUser(request);
                List<String> tokens = tokenService.tokenPersistence(newUser.getId());
                LoginResponse loginResponse = new LoginResponse(tokens.get(0), tokens.get(1),
                        new SimpleUserInfoVo(newUser.getId(), newUser.getUserName(), newUser.getPhotoUrl()));
                return loginResponse;
            }
        } else {
            thirdPartAccountUtil.loginCheckThirdPartBinded(request.getThirdPartyBindTicket(), userBaseInfoPo.getId());
            result = loginCommonService.doLogin(userBaseInfoPo, LoginTypeEnum.PHONE_VALIDCODE.getType());
            if (Strings.isBlank(result.getSecondLoginSecret())
                    && Strings.isNotBlank(request.getThirdPartyBindTicket())) {
                log.info("登录成功,且需要绑定三方");
                loginCommonService.bindThirdPartyAccount(request.getThirdPartyBindTicket(), userBaseInfoPo.getId(), false);
            }
            return result;
        }
    }
    private UserBaseInfoPo addUser(SimpleRegisterLoginRequest request) {
        UserBaseInfoPo newUser = new UserBaseInfoPo();
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setPhoneCode(request.getPhoneCode());
        newUser.setUserName(RandomUserNameUtil.getNickname());
        int i = userBaseInfoMapper.insert(newUser);
        if (i != 1) {
            log.error("新增用户失败");
            throw new BusinessException(CommonErrorCode.SERVICE_ERROR, "新增用户失败");
        }
        return newUser;
    }
}
