package com.upuphone.cloudplatform.usercenter.setting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ThirdPartyAccountSetting {

    @Value("${wx.appid:123}")
    private String wxAppId;

    @Value("${wx.secret:123}")
    private String wxSecret;

    @Value("${wx.accessTokenUrl:https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code}")
    private String accessTokenUrl;

    @Value("${wx.userInfoUrl:https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s}")
    private String userInfoUrl;
}
