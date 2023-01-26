package com.upuphone.cloudplatform.usercenter.service.common;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.model.mobile.UserInfo;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.auth.vo.AuthErrorCode;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.constants.BoundTypeEnum;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthClientDetailPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthRefreshTokenPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthClientDetailMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthRefreshTokenMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.service.userbasic.converter.UserInfoConverter;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/13
 */
@Service
@Slf4j
public class CommonService {

    private static final String MASK_MOBILE = "mask_mobile";
    private static final String USER_EMAIL = "user_email";
    private static final String CLIENT_DETAIL = "client_detail";
    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Setting setting;
    @Autowired
    private OauthClientDetailMapper clientDetailMapper;
    @Autowired
    private OauthRefreshTokenMapper refreshTokenMapper;

    /**
     * 根据userId获取用户信息（PO）
     *
     * @param userId userId
     * @return UserBaseInfoPo
     * @throws BusinessException 无用户
     */
    public UserBaseInfoPo getUserBaseInfoById(Long userId) {
        UserBaseInfoPo po = userBaseInfoMapper.selectById(userId);
        if (null == po) {
            log.error("[CommonService]用户不存在, userId=[{}]", userId);
            throw new BusinessException(UserCenterErrorCode.USER_NOT_FOUND);
        }
        return po;
    }

    /**
     * 根据userId获取用户信息(userInfo)
     *
     * @param userId userId
     * @return UserInfo
     * @see CommonService#getUserBaseInfoById
     */
    public UserInfo getUserInfoById(Long userId) {
        return UserInfoConverter.userBaseInfoPo2UserInfo(this.getUserBaseInfoById(userId));
    }

    /**
     * 根据userId和手机类型获取16位手机号 使用缓存
     *
     * @param userId    userId
     * @param phoneType 手机类型  1-安全手机 2-绑定手机
     * @return 形如008618312341234的16位手机号
     * @throws BusinessException 无手机号/无用户
     * @see BoundTypeEnum
     */
    public String getFormattedPhoneFromUserIdAndType(Long userId, Integer phoneType) {
        if (phoneType > BoundTypeEnum.BOUND_PHONE.getType()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "bound type hasn't be supported yet");
        }
        String redisKey = getMaskMobileKey(userId, phoneType);
        String formatted = stringRedisTemplate.opsForValue().get(redisKey);
        if (null == formatted) {
            UserBaseInfoPo po = this.getUserBaseInfoById(userId);
            formatted = BoundTypeEnum.SECURITY_PHONE.getType().equals(phoneType)
                    ? PhoneUtil.formatPhoneNumber(po.getSecurityPhoneCode(), po.getSecurityPhoneNumber())
                    : PhoneUtil.formatPhoneNumber(po.getPhoneCode(), po.getPhoneNumber());
            if (StringUtils.isBlank(formatted)) {
                formatted = StringUtils.EMPTY;
            }
            stringRedisTemplate.opsForValue().set(redisKey, formatted, setting.getSessionTokenDuration(), TimeUnit.SECONDS);
        }
        if (StringUtils.isBlank(formatted)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "invalid phone");
        }
        return formatted;
    }

    /**
     * 任何涉及手机号的修改执行后需调用该方法清除缓存 否则可能导致获取到的号码为修改前的手机号
     *
     * @param userId 用户ID
     * @see CommonService#getFormattedPhoneFromUserIdAndType
     */
    public void removePhoneCache(Long userId) {
        List<String> keys = Arrays.asList(getMaskMobileKey(userId, BoundTypeEnum.SECURITY_PHONE.getType()),
                getMaskMobileKey(userId, BoundTypeEnum.BOUND_PHONE.getType()));
        stringRedisTemplate.delete(keys);
    }

    /**
     * 根据userId 获取Email 使用缓存
     *
     * @param userId userId
     * @return email
     * @throws BusinessException 无email/无用户
     */
    public String getEmailFromUserId(Long userId) {
        String redisKey = getMaskEmailKey(userId);
        String email = stringRedisTemplate.opsForValue().get(redisKey);
        if (null == email) {
            UserBaseInfoPo po = this.getUserBaseInfoById(userId);
            email = po.getEmail();
            if (StringUtils.isBlank(po.getEmail())) {
                email = StringUtils.EMPTY;
            }
            stringRedisTemplate.opsForValue().set(redisKey, email, setting.getSessionTokenDuration(), TimeUnit.SECONDS);
        }
        if (StringUtils.isBlank(email)) {
            throw new BusinessException(UserCenterErrorCode.EMAIL_NOT_EXISTS);
        }
        return email;
    }

    /**
     * 任何涉及修改邮箱操作执行后需调用该方法 否则可能导致获取邮箱为过期邮箱
     *
     * @param userId 用户ID
     * @see CommonService#getEmailFromUserId
     */
    public void removeEmailCache(Long userId) {
        stringRedisTemplate.delete(getMaskEmailKey(userId));
    }

    private String getMaskMobileKey(Long userId, Integer phoneType) {
        if (null == userId || null == phoneType) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "userId/phoneType不能为空");
        }
        return RedisKeyUtils.getRedisKey(MASK_MOBILE, userId.toString(), phoneType.toString());
    }

    private String getMaskEmailKey(Long userId) {
        if (null == userId) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "userId不能为空");
        }
        return RedisKeyUtils.getRedisKey(USER_EMAIL, userId.toString());
    }

    @SneakyThrows
    public OauthClientDetailPo getClientDetailByRefreshTokenId(Long refreshTokenId) {
        if (null == refreshTokenId) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR);
        }
        List<OauthRefreshTokenPo> refreshTokenPos = refreshTokenMapper.getByRefreshIdsIncludeDeleted(Collections.singletonList(refreshTokenId));
        if (CollectionUtils.isEmpty(refreshTokenPos) || refreshTokenPos.get(0).getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(UserCenterErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        OauthRefreshTokenPo refreshTokenPo = refreshTokenPos.get(0);
        String appId = refreshTokenPo.getAppId();
        String redisKey = RedisKeyUtils.getRedisKey(CLIENT_DETAIL, appId);
        String clientDetailJson = stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(clientDetailJson)) {
            OauthClientDetailPo clientDetailPo = clientDetailMapper.selectOne(Wrappers.<OauthClientDetailPo>lambdaQuery()
                    .eq(OauthClientDetailPo::getAppId, appId));
            if (null == clientDetailPo) {
                log.error("client not found, refreshTokenId=[{}], appId=[{}]", refreshTokenPo.getId(), appId);
                throw new BusinessException(AuthErrorCode.APP_NOT_REGISTERED);
            }
            clientDetailJson = JsonUtility.toJson(clientDetailPo);
            stringRedisTemplate.opsForValue().set(redisKey, clientDetailJson, setting.getClientDuration(), TimeUnit.SECONDS);
        }
        return JsonUtility.toObject(clientDetailJson, OauthClientDetailPo.class);
    }
}
