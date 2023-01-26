package com.upuphone.cloudplatform.usercenter.remote.wechat.userinfo;

import com.upuphone.cloudplatform.common.component.BaseRemoteService;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.remote.wechat.userinfo.model.WeChatUserInfoRequest;
import com.upuphone.cloudplatform.usercenter.remote.wechat.userinfo.model.WeChatUserInfoResponse;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.model.UserThirdAccountBaseInfo;
import com.upuphone.cloudplatform.usercenter.setting.ThirdPartyAccountSetting;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class WeChatUserInfoRemoteService extends BaseRemoteService<WeChatUserInfoRequest, UserThirdAccountBaseInfo, WeChatUserInfoResponse> {

    @Autowired
    private ThirdPartyAccountSetting accountSetting;
    @Autowired
    private RestTemplate restTemplate;

    public WeChatUserInfoRemoteService(@Value("weChat") String service, @Value("userInfo") String apiName) {
        super(service, apiName);
    }

    @Override
    protected UserThirdAccountBaseInfo fromRemoteResponse(WeChatUserInfoResponse weChatUserInfoResponse) {
        if (StringUtils.isNotBlank(weChatUserInfoResponse.getErrorCode())) {
            log.error("[WeChatUserInfoRemoteService]获取微信用户信息失败, msg={}", weChatUserInfoResponse.getLogMsg());
            throw new BusinessException(CommonErrorCode.REMOTE_ERROR, "获取微信用户信息失败");
        }
        UserThirdAccountBaseInfo info = new UserThirdAccountBaseInfo();
        info.setCountry(weChatUserInfoResponse.getCountry());
        info.setHeadImgUrl(weChatUserInfoResponse.getHeadImgUrl());
        info.setNickname(weChatUserInfoResponse.getNickname());
        info.setUid(weChatUserInfoResponse.getUnionId());
        info.setSex(weChatUserInfoResponse.getSex());
        return info;
    }

    @Override
    protected WeChatUserInfoResponse processCore(WeChatUserInfoRequest weChatUserInfoRequest) throws Exception {
        if (StringUtils.isBlank(weChatUserInfoRequest.getAccessToken()) || StringUtils.isBlank(weChatUserInfoRequest.getOpenId())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "accessToken/openId不能为空");
        }
        String url = String.format(accountSetting.getUserInfoUrl(),
                weChatUserInfoRequest.getAccessToken(), weChatUserInfoRequest.getOpenId());
        return restTemplate.getForObject(url, WeChatUserInfoResponse.class);
    }

    @Override
    protected String getServiceName() {
        return "weChat";
    }

    @Override
    protected String getApiName() {
        return "userInfo";
    }
}
