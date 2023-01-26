package com.upuphone.cloudplatform.usercenter.service.userbasic.register.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TicketContext {
    private String deviceId;
    private String phoneNumber;
    private String emailAddress;
    private String validCode;
    private Date expiredDate;
}

