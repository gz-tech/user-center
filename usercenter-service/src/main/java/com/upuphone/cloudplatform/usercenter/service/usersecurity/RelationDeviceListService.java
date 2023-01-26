package com.upuphone.cloudplatform.usercenter.service.usersecurity;

import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthRefreshTokenPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthRefreshTokenMapper;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.RelationDeviceInfo;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.RelationDeviceListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/28
 */
@Service
@Slf4j
public class RelationDeviceListService {

    @Autowired
    private OauthRefreshTokenMapper refreshTokenMapper;

    private void validate() {
    }

    public RelationDeviceListResponse process() {
        validate();
        // 判断当前设备
        String deviceId = RequestContext.getDeviceId();
        // 根据refresh_token判断是否过期
        List<OauthRefreshTokenPo> validTokens = refreshTokenMapper.getValidTokens(RequestContext.getUserId());
        List<RelationDeviceInfo> relationDeviceList = new ArrayList<>();
        for (OauthRefreshTokenPo token : validTokens) {
            RelationDeviceInfo info = new RelationDeviceInfo();
            info.setDeviceId(token.getDeviceId());
            info.setId(token.getId());
            info.setModel(token.getModel());
            info.setCurrentFlag(Objects.equals(token.getDeviceId(), deviceId));
            info.setLastLoginTime(token.getCreateTime());
            info.setDeviceName(token.getDeviceName());
            info.setDeviceType(null == token.getDeviceType() ? -1 : Integer.parseInt(token.getDeviceType()));
            if (Boolean.TRUE.equals(info.getCurrentFlag())) {
                relationDeviceList.add(0, info);
            } else {
                relationDeviceList.add(info);
            }
        }
        RelationDeviceListResponse response = new RelationDeviceListResponse();
        response.setDeviceList(relationDeviceList);
        return response;
    }
}
