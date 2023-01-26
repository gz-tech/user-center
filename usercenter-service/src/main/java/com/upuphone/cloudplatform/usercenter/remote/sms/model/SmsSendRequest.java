package com.upuphone.cloudplatform.usercenter.remote.sms.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SmsSendRequest {

    private String receiverPhoneNumber;
    private String receiverPhoneCode;
    private List<String> params;
    private SmsType smsType;
    private Boolean isAsync;
}
