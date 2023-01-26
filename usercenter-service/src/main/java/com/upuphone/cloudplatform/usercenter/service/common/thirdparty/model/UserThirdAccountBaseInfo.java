package com.upuphone.cloudplatform.usercenter.service.common.thirdparty.model;

import lombok.Data;

@Data
public class UserThirdAccountBaseInfo {

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 国家
     */
    private String country;

    /**
     * 头像url
     */
    private String headImgUrl;

    /**
     * 唯一ID
     */
    private String uid;

    /** 1 为男性，2 为女性 */
    private Integer sex;
}
