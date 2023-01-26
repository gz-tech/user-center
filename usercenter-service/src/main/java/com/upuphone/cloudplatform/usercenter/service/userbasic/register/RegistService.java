package com.upuphone.cloudplatform.usercenter.service.userbasic.register;

import com.google.common.base.Strings;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.common.util.StringUtil;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.service.basic.util.CheckTextUtil;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.WeChatAccountUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.TokenService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.LoginCommonService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.model.RegistReqBo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.model.TicketContext;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.registstrategy.RegistStrategy;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.util.RandomUserNameUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.util.TicketUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.repo.AccountBindingRepo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.util.ThirdPartAccountUtil;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.SimpleUserInfoVo;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.CheckTextRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.register.RegistResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RegistService extends BaseService<RegistReqBo, RegistResponse> {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TicketUtil ticketUtil;

    @Autowired
    private RegistStrategy emailRegistSrategy;

    @Autowired
    private RegistStrategy phoneRegistSrategy;

    @Autowired
    private ThirdPartAccountUtil thirdPartAccountUtil;

    @Autowired
    private WeChatAccountUtil weChatAccountUtil;

    @Autowired
    private CheckTextUtil checkTextUtil;

    @Autowired
    private AccountBindingRepo accountBindingRepo;
    @Autowired
    private LoginCommonService loginCommonService;
    @Autowired
    private Setting setting;

    @Override
    protected void validate(RegistReqBo commonRequest) {
        if (null == RequestContext.getSessionTicket()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "ticket can not be null");
        }
        if (null == RequestContext.getDeviceId()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "deviceId in header can not be null");
        }
        if (null == RequestContext.getModel()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "device model in header can not be null");
        }
        if (null == RequestContext.getAppId()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "app id in header can not be null");
        }
        if (null == RequestContext.getDeviceName()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "device name can not be null");
        }
        if (null == RequestContext.getDeviceType()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "device type can not be null");
        }

        if (!Strings.isNullOrEmpty(commonRequest.getPhoneNumber())) {
            commonRequest.setPhoneNumber(StringUtil.replaceSpace(commonRequest.getPhoneNumber()));
        }
    }

    @Override
    protected RegistResponse processCore(RegistReqBo registRequest) throws Exception {

        registRequest.setPhoneCode(PhoneUtil.formatPhoneAreaCode(registRequest.getPhoneCode()));

        RegistStrategy registStrategy = this.getRegistStrategy(registRequest);

        String uniquePhoneOrEmail = registStrategy.getUniqueKey(registRequest);

        String lockKey = RedisKeys.registLockKey(uniquePhoneOrEmail);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(0, 10, TimeUnit.SECONDS)) {
                throw new BusinessException(CommonErrorCode.OPERATION_ERROR, "please not repeat request");
            }

            registStrategy.checkKeyNumberUnique(registRequest);
            //check ticket
            this.checkTicket(registRequest.getTicket(), uniquePhoneOrEmail);
            // add user
            UserBaseInfoPo userBaseInfoPo = this.buildUserPo(registRequest, null);
            UserBaseInfoPo registeredUser = this.addUser(userBaseInfoPo);

            if (!Strings.isNullOrEmpty(registRequest.getThirdPartAuthTicket())) {
                registeredUser = loginCommonService.bindThirdPartyAccount(registRequest.getThirdPartAuthTicket(), registeredUser.getId(), true);
            }

            // generate token
            List<String> tokens = tokenService.tokenPersistence(registeredUser.getId());
            RegistResponse result = new RegistResponse();
            result.setAccessToken(tokens.get(0));
            result.setRefreshToken(tokens.get(1));
            result.setUserId(registeredUser.getId());
            SimpleUserInfoVo simpleUserInfoVo = new SimpleUserInfoVo();
            // username 敏感词检测
            CheckTextRequest checkTextRequest  = new CheckTextRequest();
            checkTextRequest.setDataId(registeredUser.getId() + "");
            checkTextRequest.setContent(registeredUser.getUserName());
            checkTextUtil.textValidation(checkTextRequest);
            simpleUserInfoVo.setUserName(registeredUser.getUserName());
            simpleUserInfoVo.setPhotoUrl(registeredUser.getPhotoUrl());
            simpleUserInfoVo.setUserId(registeredUser.getId());
            result.setSimpleUserInfoVo(simpleUserInfoVo);
            return result;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    private RegistStrategy getRegistStrategy(RegistReqBo registRequest) {
        if (RegistReqBo.RegistType.PHONE.equals(registRequest.getRegistType())) {
            return phoneRegistSrategy;
        } else if (RegistReqBo.RegistType.EMAIL.equals(registRequest.getRegistType())) {
            return emailRegistSrategy;
        }
        return null;
    }

    private UserBaseInfoPo buildUserPo(RegistReqBo phoneRegistRequest, ThirdPartAccountUtil.ThirdPartAuthResult authResult) {
        UserBaseInfoPo userBaseInfoPo = new UserBaseInfoPo();
        userBaseInfoPo.setUserName(RandomUserNameUtil.getNickname());
        userBaseInfoPo.setPassword(BCrypt.hashpw(phoneRegistRequest.getPassword(), BCrypt.gensalt()));
        userBaseInfoPo.setPhoneCode(phoneRegistRequest.getPhoneCode());
        userBaseInfoPo.setPhoneNumber(phoneRegistRequest.getPhoneNumber());
        userBaseInfoPo.setEmail(phoneRegistRequest.getEmailAddress());
        return userBaseInfoPo;
    }

    private UserBaseInfoPo addUser(UserBaseInfoPo userBaseInfoPo) {

        int count = userBaseInfoMapper.insert(userBaseInfoPo);
        if (count <= 0) {
            throw new BusinessException(CommonErrorCode.SERVICE_ERROR, "");
        }
        return userBaseInfoPo;
    }

    private String checkTicket(String requestTicket, String uniquePhoneOrEmail) {

        String ticketKey = RedisKeys.registTicket(uniquePhoneOrEmail);
        String ticketInRedis = stringRedisTemplate.opsForValue().get(ticketKey);
        if (ticketInRedis == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "ticket expired");
        }
        if (!ticketInRedis.equals(requestTicket)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "ticket wrong");
        }

        try {
            TicketContext ticketContext = ticketUtil.parseTicket(ticketInRedis);
        } catch (ExpiredJwtException e) {
            stringRedisTemplate.delete(ticketKey);
            log.warn("ticket error " + ticketKey, e);
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "ticket expired", e);
        }
        return ticketKey;
    }
}
