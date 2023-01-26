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
 *
 * </p>
 *
 * @author wulong
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oauth_code")
public class OauthCodePo extends Model<OauthCodePo> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * app_id
     */
    private String appId;

    /**
     * user_id
     */
    private String userId;

    /**
     * Auth code
     */
    private String code;

    private String authentication;

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
