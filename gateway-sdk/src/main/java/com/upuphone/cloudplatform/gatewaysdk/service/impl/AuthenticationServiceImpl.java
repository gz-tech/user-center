package com.upuphone.cloudplatform.gatewaysdk.service.impl;

import com.google.common.base.Strings;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.gatewaysdk.pojo.AuthenticationErrorCode;
import com.upuphone.cloudplatform.gatewaysdk.pojo.TokenBO;
import com.upuphone.cloudplatform.gatewaysdk.pojo.TokenHeaderBO;
import com.upuphone.cloudplatform.gatewaysdk.service.AuthenticationService;
import com.upuphone.cloudplatform.gatewaysdk.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/10
 */
@Component
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Value("${access-token-sign-key}")
    private String accessTokenSignKey;

    private static final String LOGOUT_KEY = "USERCENTER_LOGOUT_%s";

    private static final String KICKED = "KICKED";

    private static final String DELETED = "DELETED";


    @Override
    public Mono<TokenHeaderBO> processToken(String token) {

        if (Strings.isNullOrEmpty(token)) {
            throw new BusinessException(AuthenticationErrorCode.TOKEN_EMPTY_ERROR);
        }

        TokenBO tokenBO = TokenUtil.getTokenBOFromToken(token, accessTokenSignKey);

        // 判断token是否被强制过期
        String redisKey = String.format(LOGOUT_KEY, tokenBO.getRefreshTokenId());
        String tokenState = redisTemplate.opsForValue().get(redisKey);
        if (null != tokenState) {
            if (KICKED.equals(tokenState)) {
                throw new BusinessException(AuthenticationErrorCode.TOKEN_DEVICE_FORCE_LOGOUT_ERROR);
            } else if (DELETED.equals(tokenState)) {
                throw new BusinessException(AuthenticationErrorCode.ACCOUNT_DELETED_ERROR);
            } else {
                throw new BusinessException(AuthenticationErrorCode.TOKEN_FORCE_INVALID_ERROR);
            }
        }

        TokenHeaderBO response = new TokenHeaderBO();
        response.setAppid(tokenBO.getAppid());
        response.setDeviceId(tokenBO.getDeviceId());
        response.setDeviceType(tokenBO.getDeviceType());
        response.setExpirationTime(tokenBO.getExpireTime());
        response.setUserId(Long.valueOf(tokenBO.getUserId()));
        response.setRefreshTokenId(tokenBO.getRefreshTokenId());
        return Mono.create(monoSink -> monoSink.success(response));
    }
}
