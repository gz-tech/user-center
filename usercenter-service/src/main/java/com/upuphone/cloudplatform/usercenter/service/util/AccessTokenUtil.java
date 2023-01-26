package com.upuphone.cloudplatform.usercenter.service.util;

import com.upuphone.cloudplatform.usercenter.common.util.DateUtil;
import com.upuphone.cloudplatform.usercenter.entity.DeviceInfo;
import com.upuphone.cloudplatform.usercenter.entity.token.AccessTokenBo;
import com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant;
import com.upuphone.cloudplatform.usercenter.setting.SignSetting;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant.APP_ID;
import static com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant.DEVICE_ID;
import static com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant.DEVICE_NAME;
import static com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant.DEVICE_TYPE;
import static com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant.EXPIRE_TIME_LENGTH;
import static com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant.MODEL;
import static com.upuphone.cloudplatform.usercenter.entity.token.TokenConstant.REFRESH_TOKEN_ID;

@Slf4j
@Component
public class AccessTokenUtil {

    @Autowired
    private SignSetting signSetting;

    /**
     * 根据签发时间点、过期时长生成accessToken
     *
     * @param userId         userId
     * @param issueAt        签发时间点
     * @param expireSeconds  过期时长（秒）
     * @param refreshTokenId refreshTokenId
     * @param deviceInfo     设备信息
     * @return accessToken
     */
    public String generateAccessToken(Long userId, LocalDateTime issueAt,
            Integer expireSeconds, Long refreshTokenId, DeviceInfo deviceInfo) {
        LocalDateTime expireDate = issueAt.plusSeconds(expireSeconds);
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(DateUtil.asDate(issueAt))
                .setExpiration(DateUtil.asDate(expireDate))
                .claim(DEVICE_ID, deviceInfo.getDeviceId())
                .claim(APP_ID, deviceInfo.getAppId())
                .claim(EXPIRE_TIME_LENGTH, expireSeconds)
                .claim(REFRESH_TOKEN_ID, refreshTokenId)
                .claim(MODEL, deviceInfo.getModel())
                .claim(DEVICE_NAME, deviceInfo.getDeviceName())
                .claim(DEVICE_TYPE, deviceInfo.getDeviceType())
                .signWith(SignatureAlgorithm.HS256, signSetting.getAccessTokenSignKey());
        return jwtBuilder.compact();
    }

    /**
     * 通过指定当前时间为签发时间点、过期时长生成accessToken
     *
     * @param userId         userId
     * @param expireSeconds  过期时长（秒）
     * @param refreshTokenId refreshTokenId
     * @param deviceInfo     设备信息
     * @return accessToken
     */
    public String generateAccessToken(Long userId, Integer expireSeconds, Long refreshTokenId, DeviceInfo deviceInfo) {
        LocalDateTime issueAt = LocalDateTime.now();
        return this.generateAccessToken(userId, issueAt, expireSeconds, refreshTokenId, deviceInfo);
    }

    /**
     * 根据setting中的Key解析accessToken
     *
     * @param token token
     * @return AccessTokenBo
     */
    public AccessTokenBo parseToken(String token) {
        return parseToken(token, signSetting.getAccessTokenSignKey());
    }

    /**
     * 根据指定key解析accessToken
     *
     * @param token token
     * @param key   key
     * @return accessTokenBo
     */
    public AccessTokenBo parseToken(String token, String key) {
        AccessTokenBo tokenBo = new AccessTokenBo();
        Claims claims = TokenUtil.getClaimAndSetToTokenBO(token, key, tokenBo);
        tokenBo.setAppid(claims.get(TokenConstant.APP_ID).toString());
        tokenBo.setUserId(claims.getSubject());
        tokenBo.setDeviceId(claims.get(TokenConstant.DEVICE_ID).toString());
        tokenBo.setModel(claims.get(TokenConstant.MODEL).toString());
        tokenBo.setStartValidTime(LocalDateTime.ofInstant(claims.getIssuedAt().toInstant(), ZoneId.systemDefault()));
        tokenBo.setExpireTime(DateUtil.asLocalDateTime(claims.getExpiration()));
        tokenBo.setExpireTimeLength(claims.get(TokenConstant.EXPIRE_TIME_LENGTH, Long.class));
        tokenBo.setRefreshTokenId(claims.get(REFRESH_TOKEN_ID).toString());
        return tokenBo;
    }
}
