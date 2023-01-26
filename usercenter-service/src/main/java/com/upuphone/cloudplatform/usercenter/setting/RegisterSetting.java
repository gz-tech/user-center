package com.upuphone.cloudplatform.usercenter.setting;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class RegisterSetting {

    @Value("${regist.ticket.valid.duration:300}")
    private Integer registTicketValidDuration;

    @Value("${regist.ticket.sign.key:registSignKey}")
    private String signKey;

    @Value("${register.mode:normal}")
    private String mode;
}
