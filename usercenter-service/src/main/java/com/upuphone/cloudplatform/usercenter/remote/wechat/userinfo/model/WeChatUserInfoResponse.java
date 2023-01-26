package com.upuphone.cloudplatform.usercenter.remote.wechat.userinfo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.upuphone.cloudplatform.usercenter.remote.wechat.model.WeChatErrorResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WeChatUserInfoResponse extends WeChatErrorResponse {

    @JsonProperty("openid")
    private String openId;

    private String nickname;

    //1 为男性，2 为女性
    private Integer sex;

    private String province;

    private String city;

    private String country;

    @JsonProperty("headimgurl")
    private String headImgUrl;

    private List<String> privilege;

    @JsonProperty("unionid")
    private String unionId;
}
