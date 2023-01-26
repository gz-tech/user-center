package com.upuphone.cloudplatform.usercenter.service.userbasic.login;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.common.redis.RedisKeys;
import com.upuphone.cloudplatform.usercenter.common.util.MaskUtil;
import com.upuphone.cloudplatform.usercenter.common.util.encryption.AESUtil;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthRefreshTokenPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthRefreshTokenMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserThirdPartyAccountMapper;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.WeChatAccountUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.TokenService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.repo.AccountBindingRepo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.util.ThirdPartAccountUtil;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import com.upuphone.cloudplatform.usercenter.vo.SimpleUserInfoVo;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author guangzheng.ding
 * @date 2021/12/19 13:44
 */
@Service
@Slf4j
public class LoginCommonService {

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private OauthRefreshTokenMapper oauthRefreshTokenMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private Setting setting;
    @Autowired
    private ThirdPartAccountUtil thirdPartAccountUtil;
    @Autowired
    private UserThirdPartyAccountMapper userThirdPartyAccountMapper;
    @Autowired
    private WeChatAccountUtil weChatAccountUtil;
    @Autowired
    private AccountBindingRepo accountBindingRepo;


    public Boolean newDeviceCheck(Long userId) {
        QueryWrapper<OauthRefreshTokenPo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if (oauthRefreshTokenMapper.getByUserIdAndDevice(userId, null).size() == 0) {
            log.info("userId：{}首次登录", userId);
            return false;
        }
        List<OauthRefreshTokenPo> oauthRefreshTokenPos = oauthRefreshTokenMapper.getByUserIdAndDevice(
                userId, RequestContext.getDeviceId());
        if (oauthRefreshTokenPos.size() == 0) {
            log.info("未找到deviceId一样的refreshToken，需要二次校验");
            return true;
        } else {
            log.info("找到deviceId一样的refreshToken，不需要二次校验");
            return false;
        }
    }

    /**
     * 根据refreshToken判断是否已登录
     *
     * @param userId
     */
    public OauthRefreshTokenPo hasLoginCheck(Long userId) {
        log.info("用userId和deviceId查找refreshToken来判断是否已登录");
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("user_id", userId);
        queryParam.put("device_id", RequestContext.getDeviceId());
        List<OauthRefreshTokenPo> refreshTokenPos = oauthRefreshTokenMapper.selectByMap(queryParam);
        if (CollectionUtils.isEmpty(refreshTokenPos)) {
            return null;
        } else {
            return refreshTokenPos.get(0);
        }
    }

    public LoginResponse doLogin(UserBaseInfoPo user, String loginType) {
        OauthRefreshTokenPo existRefreshToken = hasLoginCheck(user.getId());
        if (existRefreshToken != null) {
            String accessToken = tokenService.generateAccessToken(user.getId(), existRefreshToken.getId());
            LoginResponse loginResponse = new LoginResponse(accessToken, existRefreshToken.getRefreshToken(),
                    new SimpleUserInfoVo(user.getId(), user.getUserName(), user.getPhotoUrl()));
            return loginResponse;
        }
        //        Boolean isSecondLogin = secondLoginCheck(secondLoginSecret, userId, deviceId, loginType);
        if (newDeviceCheck(user.getId())) {
            // need double-check
            String secretForUser = UUID.randomUUID().toString() + "+" + user.getId();
            String secretInRedis = secretForUser + "-" + loginType;
            stringRedisTemplate.opsForValue().set(RedisKeys.getUserSecondLoginKey(user.getId(), RequestContext.getDeviceId()),
                    secretInRedis, setting.getLoginCheckExpireSeconds(), TimeUnit.SECONDS);
            LoginResponse response = new LoginResponse();
            response.setSecondLoginSecret(secretForUser);
            response.setSimpleUserInfoVo(new SimpleUserInfoVo(user.getId(), null, null));
            UserBaseInfoPo userBaseInfoPo = userBaseInfoMapper.selectById(user.getId());
            if (userBaseInfoPo.getPhoneNumber() != null) {
                response.setPhoneNumber(MaskUtil.maskPhone(userBaseInfoPo.getPhoneNumber()));
            }
            if (userBaseInfoPo.getSecurityPhoneNumber() != null) {
                response.setSecurityPhoneNumber(MaskUtil.maskPhone(userBaseInfoPo.getSecurityPhoneNumber()));
            }
            if (userBaseInfoPo.getEmail() != null) {
                response.setEmailAddress(MaskUtil.maskEmail(userBaseInfoPo.getEmail()));
            }
            return response;
        }
        List<String> tokens = tokenService.tokenPersistence(user.getId());
        LoginResponse loginResponse = new LoginResponse(tokens.get(0), tokens.get(1),
                new SimpleUserInfoVo(user.getId(), user.getUserName(), user.getPhotoUrl()));
        return loginResponse;
    }

    public void loginHeaderCheck() {
        if (null == RequestContext.getModel()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "model id can not be null");
        }
        if (null == RequestContext.getDeviceId()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "device id can not be null");
        }
        if (null == RequestContext.getAppId()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "app id can not be null");
        } else {
            if (!setting.getSharedLoginAppId().contains(RequestContext.getAppId())) {
                throw new BusinessException(UserCenterErrorCode.APP_NOT_SUPPORT_LOGIN);
            }
        }
        if (null == RequestContext.getDeviceName()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "device name can not be null");
        }
        if (null == RequestContext.getDeviceType()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "device type can not be null");
        }
    }

    @Transactional(rollbackFor = Exception.class, timeout = 100)
    public UserBaseInfoPo bindThirdPartyAccount(String ticket, Long userId, boolean isRegister) {
        ThirdPartAccountUtil.ThirdPartAuthResult thirdPartAuthResult = thirdPartAccountUtil.getThirdPartAuthResultFromTicket(ticket);
        accountBindingRepo.bindedCheck(userId, thirdPartAuthResult.getAccountInfo().getUid(), thirdPartAuthResult.getAccountType());
        accountBindingRepo.insertThirdPartUser(userId, thirdPartAuthResult.getAccountInfo().getUid(), thirdPartAuthResult.getAccountType());
        UserBaseInfoPo user = userBaseInfoMapper.selectById(userId);
        if (isRegister || StringUtils.isEmpty(user.getPhotoUrl())) {
            if (!StringUtils.isEmpty(thirdPartAuthResult.getAccountInfo().getHeadImgUrl())) {
                String newPhotoUrl = weChatAccountUtil.uploadWechatPhoto(thirdPartAuthResult.getAccountInfo().getHeadImgUrl());
                user.setPhotoUrl(newPhotoUrl);
            }
        }
        if (isRegister || user.getGender().equals(0)) {
            user.setGender(thirdPartAuthResult.getAccountInfo().getSex());
        }
        if (isRegister || StringUtils.isEmpty(user.getUserName())) {
            user.setUserName(thirdPartAuthResult.getAccountInfo().getNickname());
        }
        userBaseInfoMapper.updateById(user);
        String thirdBindRedisKey = ThirdPartAccountUtil.getRedisKey(
                AESUtil.decrypt(ticket, setting.getThirdLoginTicketAesKey(), setting.getThirdLoginTicketAesIV()));
        stringRedisTemplate.delete(thirdBindRedisKey);
        return user;
    }
}
