package com.upuphone.cloudplatform.usercenter.remote.sms.model;

import com.upuphone.cloudplatform.usercenter.remote.RemoteResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsSendResponse implements RemoteResponse {
    private boolean success;

    public SmsSendResponse(boolean success) {
        this.success = success;
    }

    public SmsSendResponse() {
    }
}
