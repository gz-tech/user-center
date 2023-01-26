package com.upuphone.cloudplatform.usercenter.service.util;

import com.google.common.base.Strings;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.common.util.DateUtil;
import com.upuphone.cloudplatform.usercenter.common.util.encryption.AESUtil;
import com.upuphone.cloudplatform.usercenter.entity.DeviceInfo;
import com.upuphone.cloudplatform.usercenter.entity.token.RefreshTokenBo;
import com.upuphone.cloudplatform.usercenter.entity.token.TokenBo;
import com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant.APP_ID;
import static com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant.DEVICE_ID;
import static com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant.DEVICE_TYPE;
import static com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant.EXPIRE_TIME_LENGTH;
import static com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant.MODEL;

@Slf4j
@Component
public class RefreshTokenUtil {

    @Autowired
    private Setting setting;

    /**
     * 根据setting的aesKey解密refreshToken
     *
     * @param refreshToken refreshToken
     * @return TokenBo
     */
    public TokenBo decryptRefreshToken(String refreshToken) {
        if (Strings.isNullOrEmpty(refreshToken)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "refresh token can not be null");
        }
        String decryptedRefreshToken = AESUtil.decrypt(refreshToken, setting.getRefreshTokenAesKey(), setting.getRefreshTokenAesIV());
        TokenBo tokenBO = this.parseToken(decryptedRefreshToken, setting.getRefreshTokenSign());
        tokenBO.setTokenStr(refreshToken);
        return tokenBO;
    }

    /**
     * 根据从生成时间起指定有效时长生成refreshToken
     *
     * @param userId        userId
     * @param expireSeconds 过期时间秒数
     * @param deviceInfo    设备信息
     * @return refreshToken
     */
    public String generateRefreshToken(Long userId, Integer expireSeconds, DeviceInfo deviceInfo) {
        LocalDateTime issueAt = LocalDateTime.now();
        LocalDateTime expireDate = issueAt.plusSeconds(expireSeconds);
        return this.generateRefreshToken(userId, issueAt, expireDate, deviceInfo);
    }

    /**
     * 根据过期时间点生成refreshToken
     *
     * @param userId     userId
     * @param expireDate 过期时间点LocalDateTime
     * @param deviceInfo 设备信息
     * @return refreshToken
     */
    public String generateRefreshToken(Long userId, LocalDateTime expireDate, DeviceInfo deviceInfo) {
        LocalDateTime issueAt = LocalDateTime.now();
        return this.generateRefreshToken(userId, issueAt, expireDate, deviceInfo);
    }

    /**
     * 根据签发时间、过期时间点生成refreshToken
     *
     * @param userId     userId
     * @param issueAt    签发时间
     * @param expireDate 过期时间点LocalDateTime
     * @param deviceInfo 设备信息
     * @return refreshToken
     */
    public String generateRefreshToken(Long userId, LocalDateTime issueAt, LocalDateTime expireDate, DeviceInfo deviceInfo) {
        if (issueAt != null && issueAt.isAfter(expireDate)) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "expire time can not before issue time");
        }
        if (null == issueAt) {
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR, "issue time cannot be null");
        }
        Long expireSeconds = issueAt.until(expireDate, ChronoUnit.SECONDS);
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(DateUtil.asDate(issueAt))
                .setExpiration(DateUtil.asDate(expireDate))
                .claim(DEVICE_ID, deviceInfo.getDeviceId())
                .claim(APP_ID, deviceInfo.getAppId())
                .claim(EXPIRE_TIME_LENGTH, expireSeconds)
                .claim(MODEL, deviceInfo.getModel())
                .claim(DEVICE_TYPE, deviceInfo.getDeviceType())
                .signWith(SignatureAlgorithm.HS256, setting.getRefreshTokenSign());
        String refreshToken = jwtBuilder.compact();
        return AESUtil.encrypt(refreshToken, setting.getRefreshTokenAesKey(), setting.getRefreshTokenAesIV());
    }

    /**
     * 根据签名key和token解析出refreshTokenBO
     *
     * @param token token字符串
     * @param key   签名key
     * @return RefreshTokenBo
     */
    public RefreshTokenBo parseToken(String token, String key) {
        RefreshTokenBo tokenBo = new RefreshTokenBo();
        Claims claims = TokenUtil.getClaimAndSetToTokenBO(token, key, tokenBo);
        tokenBo.setAppid(claims.get(TokenConstant.APP_ID).toString());
        tokenBo.setUserId(claims.getSubject());
        tokenBo.setDeviceId(claims.get(TokenConstant.DEVICE_ID).toString());
        tokenBo.setModel(claims.get(TokenConstant.MODEL).toString());
        tokenBo.setDeviceType(claims.get(DEVICE_TYPE).toString());
        tokenBo.setStartValidTime(LocalDateTime.ofInstant(claims.getIssuedAt().toInstant(), ZoneId.systemDefault()));
        tokenBo.setExpireTimeLength(claims.get(TokenConstant.EXPIRE_TIME_LENGTH, Long.class));
        tokenBo.setExpireTime(DateUtil.asLocalDateTime(claims.getExpiration()));
        return tokenBo;
    }
}
