package com.upuphone.cloudplatform.usercenter.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Oath client detail
 * </p>
 *
 * @author wulong
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oauth_client_detail")
public class OauthClientDetailPo extends Model<OauthClientDetailPo> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * App unique id
     */
    private String appId;

    /**
     * Client secret
     */
    private String clientSecret;

    /**
     * client_credentials,password,authorization_code,implicit
     */
    private String authorizedGrantTypes;

    /**
     * Redirect url
     */
    private String webServerRedirectUri;

    /**
     * The access token validity period int second for this client.
     * 0 stands for always
     */
    private Integer accessTokenValidity;

    /**
     * The refresh token validity period int second for this client.
     * 0 stands for always
     */
    private Integer refreshTokenValidity;

    /**
     * Test whether client needs user approval for a particular scope.0-auto; 1 not auto
     */
    @TableField("autoapprove")
    private Boolean autoApprove;

    /**
     * 授权码有效时间（秒）
     */
    private Integer codeValidity;

    /**
     * 0 valid 1 deleted
     */
    private Integer delFlag;

    /**
     * create_time
     */
    private LocalDateTime createTime;

    /**
     * update_time
     */
    private LocalDateTime updateTime;


    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
