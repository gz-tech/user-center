package com.upuphone.cloudplatform.usercenter.service.userbasic.logout;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.constants.LoginTypeEnum;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.UserBaseInfoPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthRefreshTokenMapper;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.UserBaseInfoMapper;
import com.upuphone.cloudplatform.usercenter.redis.RedisKeyUtils;
import com.upuphone.cloudplatform.usercenter.service.userbasic.logout.model.LogoutReqVo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.util.PasswordUtil;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.util.UserSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author guangzheng.ding
 * @date 2021/12/20 20:00
 */
@Service
@Slf4j
public class LogoutService extends BaseService<LogoutReqVo, Void> {

    @Autowired
    private OauthRefreshTokenMapper oauthRefreshTokenMapper;

    @Autowired
    private UserBaseInfoMapper userBaseInfoMapper;

    @Autowired
    private UserSecurityUtils userSecurityUtils;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PasswordUtil passwordUtil;

    @Override
    protected void validate(LogoutReqVo logoutRequest) {
        if (StringUtils.isBlank(RequestContext.getDeviceId())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "deviceId不能为空");
        }
        if (null == RequestContext.getUserId()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "user id不能为空");
        }
        if (StringUtils.isBlank(RequestContext.getRefreshTokenId())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "refreshTokenId不能为空");
        }
    }

    @Override
    protected Void processCore(LogoutReqVo logoutRequest) throws Exception {
        String deviceId = RequestContext.getDeviceId();
        Long userId = RequestContext.getUserId();
        String lockKey = RedisKeyUtils.getIdempotentCheckKey(LoginTypeEnum.LOGOUT, userId, deviceId);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(0, 30, TimeUnit.SECONDS)) {
                throw new BusinessException(CommonErrorCode.OPERATION_ERROR, "please not repeat request");
            }
            UserBaseInfoPo userBaseInfoPo = userBaseInfoMapper.selectById(userId);
            // 校验UserId & password 不再通过accessToken判断
            if (LogoutReqVo.LogoutType.APP.equals(logoutRequest.getLogoutType())) {
                passwordUtil.checkPassword(userId, logoutRequest.getPassword(), userBaseInfoPo.getPassword());
            }

            // 通过deviceId+userId获取refreshId
            Long refreshTokenId = Long.valueOf(RequestContext.getRefreshTokenId());
            if (0 == oauthRefreshTokenMapper.deleteById(refreshTokenId)) {
                log.error("[LogoutService]删除refreshToken失败，deviceId=[{}], userId=[{}], refreshTokenId=[{}]",
                        deviceId, userId, refreshTokenId);
                throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, "删除refreshToken失败");
            }
            // 失效accessToken
            userSecurityUtils.addLogoutKey(refreshTokenId);
            return null;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
