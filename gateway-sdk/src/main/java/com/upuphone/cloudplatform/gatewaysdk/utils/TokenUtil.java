package com.upuphone.cloudplatform.gatewaysdk.utils;

import com.google.common.base.MoreObjects;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.gatewaysdk.pojo.AuthenticationErrorCode;
import com.upuphone.cloudplatform.gatewaysdk.pojo.TokenBO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author guangzheng.ding
 * @date 2021/12/14 18:12
 */
@Slf4j
public class TokenUtil {

    public static final String REFRESH_TOKEN_ID = "refreshTokenId";

    public static final String DEVICE_TYPE = "deviceType";

    public static final String APP_ID = "appid";

    public static final String DEVICE_ID = "deviceId";


    /**
     * @param token
     * @param signKey
     * @return
     * @throws BusinessException
     */
    public static TokenBO getTokenBOFromToken(String token, String signKey) {
        TokenBO bo = new TokenBO();
        Claims body = null;
        try {
            body = Jwts.parser()
                    .setSigningKey(signKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 过期
            throw new BusinessException(AuthenticationErrorCode.TOKEN_EXPIRED_ERROR);
        } catch (UnsupportedJwtException e) {
            // 无效
            throw new BusinessException(AuthenticationErrorCode.TOKEN_ILLEGAL_ERROR);
        } catch (Exception e) {
            log.info("[TokenUtil]解析token出错, token=[{}], e:", token, e);
            throw new BusinessException(AuthenticationErrorCode.TOKEN_PARSE_ERROR);
        }
        bo.setUserId(body.getSubject());
        bo.setDeviceId(body.get(DEVICE_ID).toString());
        bo.setDeviceType(MoreObjects.firstNonNull(body.get(DEVICE_TYPE), "-1").toString());
        bo.setExpireTime(body.getExpiration().getTime());
        bo.setAppid(body.get(APP_ID).toString());
        bo.setRefreshTokenId(body.get(REFRESH_TOKEN_ID).toString());
        return bo;
    }
}

