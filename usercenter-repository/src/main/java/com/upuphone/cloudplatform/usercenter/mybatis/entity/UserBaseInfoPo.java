package com.upuphone.cloudplatform.usercenter.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_base_info")
public class UserBaseInfoPo extends Model<UserBaseInfoPo> {

    private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String boxingId;

    /**
     * User name
     */
    private String userName;

    private String phoneCode;
    /**
     * Phone number
     */
    private String phoneNumber;

    private String securityPhoneCode;

    /**
     * Security phone number
     */
    private String securityPhoneNumber;

    private String email;

    private String password;

    /**
     * 0 male 1 famale
     */
    private Integer gender;

    /**
     * Country code
     */
    private String countryCode;

    /**
     * Country name
     */
    private String countryName;

    private String photoUrl;

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
