package com.upuphone.cloudplatform.usercenter.service.userbasic.login.thirdparty;

import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.WeChatAccountUtil;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.model.UserThirdAccountBaseInfo;
import com.upuphone.cloudplatform.usercenter.service.common.thirdparty.model.WeChatTokenBo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.model.ThirdPartyAccessTokenBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WechatLoginServiceImpl implements ThirdPartyLoginService {


    @Autowired
    private WeChatAccountUtil weChatAccountUtil;

    @Override
    public ThirdPartyAccessTokenBo getThirdPartyAccessTokenByCode(String code) {
        WeChatTokenBo weChatTokenBo = weChatAccountUtil.getAccessTokenAndOpenIdByCode(code);
        ThirdPartyAccessTokenBo thirdPartyAccessTokenBo = new ThirdPartyAccessTokenBo();
        thirdPartyAccessTokenBo.setAccessToken(weChatTokenBo.getAccessToken());
        thirdPartyAccessTokenBo.setOpenId(weChatTokenBo.getOpenId());
        return thirdPartyAccessTokenBo;
    }

    @Override
    public UserThirdAccountBaseInfo getThirdPartyUserInfoByAccessToken(ThirdPartyAccessTokenBo bo) {
        return weChatAccountUtil.getUserInfo(bo.getAccessToken(), bo.getOpenId());
    }
}
