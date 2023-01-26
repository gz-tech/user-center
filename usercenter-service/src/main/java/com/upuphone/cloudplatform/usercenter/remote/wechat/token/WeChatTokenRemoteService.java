package com.upuphone.cloudplatform.usercenter.remote.wechat.token;

import com.upuphone.cloudplatform.common.component.BaseRemoteService;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.remote.wechat.token.model.WeChatTokenRequest;
import com.upuphone.cloudplatform.usercenter.remote.wechat.token.model.WeChatTokenResponse;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.model.WeChatTokenBo;
import com.upuphone.cloudplatform.usercenter.setting.ThirdPartyAccountSetting;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class WeChatTokenRemoteService extends BaseRemoteService<WeChatTokenRequest, WeChatTokenBo, WeChatTokenResponse> {

    @Autowired
    private ThirdPartyAccountSetting accountSetting;
    @Autowired
    private RestTemplate restTemplate;

    public WeChatTokenRemoteService(@Value("weChat") String service, @Value("accessToken") String apiName) {
        super(service, apiName);
    }

    @Override
    protected WeChatTokenBo fromRemoteResponse(WeChatTokenResponse weChatTokenResponse) {
        if (StringUtils.isNotBlank(weChatTokenResponse.getErrorCode())) {
            log.error("[WeChatUserInfoRemoteService]获取微信token失败, msg={}", weChatTokenResponse.getLogMsg());
            throw new BusinessException(CommonErrorCode.REMOTE_ERROR, "获取微信token失败");
        }
        WeChatTokenBo bo = new WeChatTokenBo();
        bo.setAccessToken(weChatTokenResponse.getAccessToken());
        bo.setOpenId(weChatTokenResponse.getOpenId());
        return bo;
    }

    @Override
    protected WeChatTokenResponse processCore(WeChatTokenRequest weChatTokenRequest) throws Exception {
        if (StringUtils.isBlank(weChatTokenRequest.getCode())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "code不能为空");
        }
        String url = String.format(accountSetting.getAccessTokenUrl(),
                accountSetting.getWxAppId(), accountSetting.getWxSecret(), weChatTokenRequest.getCode());
        return restTemplate.getForObject(url, WeChatTokenResponse.class);
    }

    @Override
    protected String getServiceName() {
        return "weChat";
    }

    @Override
    protected String getApiName() {
        return "accessToken";
    }
}
