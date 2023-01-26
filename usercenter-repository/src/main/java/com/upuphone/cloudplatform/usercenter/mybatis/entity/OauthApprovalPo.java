package com.upuphone.cloudplatform.usercenter.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * oauth approvals
 * </p>
 *
 * @author wulong
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oauth_approval")
public class OauthApprovalPo extends Model<OauthApprovalPo> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 0 valid 1 deleted
     */
    private Long userId;

    /**
     * client_id
     */
    private String appId;

    /**
     * 0 valid 1 deleted
     */
    private Integer delFlag;

    /**
     * expires_at
     */
    private LocalDateTime expiresAt;

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
