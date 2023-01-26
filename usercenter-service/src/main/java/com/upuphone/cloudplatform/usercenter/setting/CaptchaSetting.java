package com.upuphone.cloudplatform.usercenter.setting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * captchasetting
 */
@Component
@Getter
@Setter
public class CaptchaSetting {
    @Value("${captcha.captchaId:bb36fcc261dc43e69007dfbaa1a3c0b0}")
    private String captchaId;

    @Value("${captcha.secretId:73f0b6ff9e96b4bbcc2eb5df010efd7d}")
    private String secretId;

    @Value("${captcha.secretKey:087acc7043a954aab5a1dc48d6b697db}")
    private String secretKey;
    @Value("${captcha.url:http://c.dun.163yun.com/api/v2/verify}")
    private String captChaUrl;
}
