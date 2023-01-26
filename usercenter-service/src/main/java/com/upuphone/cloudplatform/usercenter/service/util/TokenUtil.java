package com.upuphone.cloudplatform.usercenter.service.util;

import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.entity.token.TokenBo;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/27
 */
@Slf4j
public final class TokenUtil {

    private TokenUtil() {
    }

    public static Claims getClaimAndSetToTokenBO(String token, String key, TokenBo tokenBo) {
        tokenBo.setExpired(false);
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("token过期");
            tokenBo.setExpired(true);
            claims = e.getClaims();
        } catch (UnsupportedJwtException e) {
            log.info("无效token");
            throw new BusinessException(UserCenterErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.info("解析token出错");
            throw new BusinessException(UserCenterErrorCode.AUTHENTICATION_ERROR, e.getMessage());
        }
        return claims;
    }
}
