package com.upuphone.cloudplatform.usercenter.remote.sms;

import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.message.api.dto.response.SendResponse;
import com.upuphone.cloudplatform.usercenter.remote.RetryRemoteService;
import com.upuphone.cloudplatform.usercenter.remote.sms.model.SmsSendRequest;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component("sendSmsRetryRemoteService")
public class SendSmsRetryRemoteService extends SendSmsRemoteService implements RetryRemoteService {

    public SendSmsRetryRemoteService(@Value("cloud-message-web") String service, @Value("message-send-retry") String apiName) {
        super(service, apiName);
    }

    @Override
    @Retryable(include = {FeignException.class}, maxAttempts = 3,
            backoff = @Backoff(value = 2000, multiplier = 1.5))
    protected CommonResponse<SendResponse> processCore(SmsSendRequest smsSendRequest) throws Exception {
        return super.processCore(smsSendRequest);
    }

    @Override
    protected String getApiName() {
        return "message-send-retry";
    }
}
