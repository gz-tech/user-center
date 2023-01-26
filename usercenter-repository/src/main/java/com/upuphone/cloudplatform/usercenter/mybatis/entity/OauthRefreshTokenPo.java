package com.upuphone.cloudplatform.usercenter.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * refresh_token
 * </p>
 *
 * @author wulong
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oauth_refresh_token")
public class OauthRefreshTokenPo extends Model<OauthRefreshTokenPo> {

    private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * user id
     */
    private Long userId;

    /**
     * Oauth client detail id
     */
    private String appId;

    /**
     * authentication_id
     */
    private String authenticationId;

    private String authentication;

    private String refreshToken;

    private String deviceId;

    private String deviceType;

    private String deviceName;
    /**
     * 0 valid 1 deleted
     */
    @TableLogic
    private Integer delFlag;

    /**
     * create_time
     */
    private LocalDateTime createTime;

    /**
     * update_time
     */
    private LocalDateTime updateTime;

    private String model;

    private LocalDateTime expireTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

    public OauthRefreshTokenPo() {
    }

    public OauthRefreshTokenPo(Long userId, String appId, String deviceId,
            String model, LocalDateTime date, String refreshToken,
            String deviceName, String deviceType) {
        this.userId = userId;
        this.appId = appId;
        this.deviceId = deviceId;
        this.refreshToken = refreshToken;
        this.model = model;
        this.expireTime = date;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
    }
}
