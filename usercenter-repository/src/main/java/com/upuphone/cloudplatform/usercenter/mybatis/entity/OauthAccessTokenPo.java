package com.upuphone.cloudplatform.usercenter.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * access_token
 * </p>
 *
 * @author wulong
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oauth_access_token")
public class OauthAccessTokenPo extends Model<OauthAccessTokenPo> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * user id
     */
    private Long userId;

    /**
     * Oauth client detail id
     */
    private String appId;

    private String deviceId;
    /**
     * authentication_id
     */
    private String authenticationId;

    private String accessToken;

    private Long refreshTokenId;

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

    public OauthAccessTokenPo() {
    }

    public OauthAccessTokenPo(Long userId, String appid, String deviceId, String model,
            LocalDateTime date, String authenticationId, String accessToken, Long refreshTokenId) {
        this.userId = userId;
        this.appId = appid;
        this.deviceId = deviceId;
        this.authenticationId = authenticationId;
        this.accessToken = accessToken;
        this.refreshTokenId = refreshTokenId;
        this.expireTime = date;
        this.model = model;
    }

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
