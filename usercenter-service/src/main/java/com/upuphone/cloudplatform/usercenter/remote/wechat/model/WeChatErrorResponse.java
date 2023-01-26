package com.upuphone.cloudplatform.usercenter.remote.wechat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeChatErrorResponse {

    @JsonProperty("errcode")
    private String errorCode;

    @JsonProperty("errmsg")
    private String errorMsg;

    public String getLogMsg() {
        return "errorCode:[" + errorCode + "], errorMsg:[" + errorMsg + "]";
    }
}
