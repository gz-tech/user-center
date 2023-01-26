package com.upuphone.cloudplatform.usercenter.service.authentication.model;

import com.upuphone.cloudplatform.usercenter.entity.DeviceInfo;
import com.upuphone.cloudplatform.usercenter.entity.token.TokenBo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthClientDetailPo;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthRefreshTokenPo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshProcessContext {
    private TokenBo requestTokenBO;
    // 是否使用的是上一次的refreshToken，用于意外情况刷新
    private boolean isLastReqRefreshToken;
    private OauthClientDetailPo clientDetailPo;
    private Long userId;
    private DeviceInfo deviceInfo;
    private OauthRefreshTokenPo validTokenPo;
}
