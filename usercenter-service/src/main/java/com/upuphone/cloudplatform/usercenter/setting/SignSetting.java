package com.upuphone.cloudplatform.usercenter.setting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class SignSetting {

    @Value("${access-token-sign-key}")
    private String accessTokenSignKey;
}
