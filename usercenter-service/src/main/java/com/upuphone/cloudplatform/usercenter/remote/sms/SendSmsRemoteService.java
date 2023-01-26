package com.upuphone.cloudplatform.usercenter.remote.sms;

import com.upuphone.cloudplatform.common.component.BaseRemoteService;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.message.api.MsgSendClientApi;
import com.upuphone.cloudplatform.message.api.dto.request.SmsRequest;
import com.upuphone.cloudplatform.message.api.dto.response.SendResponse;
import com.upuphone.cloudplatform.message.api.dto.sms.SmsContent;
import com.upuphone.cloudplatform.message.api.dto.sms.SmsMessageDTO;
import com.upuphone.cloudplatform.usercenter.common.util.PhoneUtil;
import com.upuphone.cloudplatform.usercenter.remote.sms.model.SmsSendRequest;
import com.upuphone.cloudplatform.usercenter.remote.sms.model.SmsSendResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/3
 */
@Service
public class SendSmsRemoteService extends BaseRemoteService<SmsSendRequest, SmsSendResponse, CommonResponse<SendResponse>> {

    @Autowired
    private MsgSendClientApi msgSendClient;
    @Value("${app.id}")
    private String appId;

    public SendSmsRemoteService(@Value("cloud-message-web") String service, @Value("message-send") String apiName) {
        super(service, apiName);
    }

    @Override
    protected SmsSendResponse fromRemoteResponse(CommonResponse<SendResponse> smsSendRemoteResponse) {
        if (null == smsSendRemoteResponse) {
            // 异步操作无返回值
            return new SmsSendResponse(true);
        }
        return new SmsSendResponse(Objects.equals(smsSendRemoteResponse.getCode(), CommonErrorCode.SUCCESS.getErrorCode()));
    }

    @Override
    protected CommonResponse<SendResponse> processCore(SmsSendRequest smsSendRequest) throws Exception {
        SmsMessageDTO messageDTO = SmsMessageDTO.builder()
                .smsContents(Collections.singletonList(SmsContent.builder()
                        .phoneCode("+" + PhoneUtil.removeFormatAreaCode(smsSendRequest.getReceiverPhoneCode()))
                        .templateCode(smsSendRequest.getSmsType().getTemplateCode())
                        .templateParas(getParams(smsSendRequest.getParams()))
                        .to(Collections.singletonList(smsSendRequest.getReceiverPhoneNumber()))
                        .build()))
                .build();
        SmsRequest remoteRequest = SmsRequest.builder()
                .appId(appId)
                .smsMessageDTO(messageDTO)
                .build();
        if (Boolean.TRUE.equals(smsSendRequest.getIsAsync())) {
            msgSendClient.asyncSendSms(remoteRequest);
            return null;
        }
        return msgSendClient.syncSendSms(remoteRequest);
    }

    @Override
    protected String getServiceName() {
        return "cloud-message-web";
    }

    @Override
    protected String getApiName() {
        return "message-send";
    }

    private LinkedHashMap<String, String> getParams(List<String> params) {
        LinkedHashMap<String, String> res = new LinkedHashMap<>();
        for (int i = 0; i < params.size(); ++i) {
            res.put(String.valueOf(i), params.get(i));
        }
        return res;
    }
}
