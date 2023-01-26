package com.upuphone.cloudplatform.usercenter.service.open;

import com.upuphone.cloudplatform.common.component.BaseService;
import com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.request.UserDeviceRequest;
import com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.response.UserDeviceResponse;
import com.upuphone.cloudplatform.usercenter.api.vo.DeviceInfoVo;
import com.upuphone.cloudplatform.usercenter.common.util.OrikaUtil;
import com.upuphone.cloudplatform.usercenter.mybatis.entity.OauthRefreshTokenPo;
import com.upuphone.cloudplatform.usercenter.mybatis.mapper.OauthRefreshTokenMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserDeviceService extends BaseService<UserDeviceRequest, UserDeviceResponse> {

    @Autowired
    private OauthRefreshTokenMapper refreshTokenMapper;

    @Override
    protected void validate(UserDeviceRequest userDeviceRequest) {

    }

    @Override
    protected UserDeviceResponse processCore(UserDeviceRequest userDeviceRequest) throws Exception {
        Long userId = Long.valueOf(userDeviceRequest.getUserId());
        List<OauthRefreshTokenPo> poList = refreshTokenMapper.getAllDevicesByUserId(userId);
        UserDeviceResponse response = new UserDeviceResponse();
        List<DeviceInfoVo> vos = new ArrayList<>();
        if (CollectionUtils.isEmpty(poList)) {
            response.setDevices(vos);
            return response;
        }
        Set<String> uniqueIds = new HashSet<>();
        for (OauthRefreshTokenPo po : poList) {
            String uniqueId = po.getDeviceId() + po.getDeviceType();
            if (uniqueIds.contains(uniqueId)) {
                continue;
            }
            uniqueIds.add(uniqueId);
            DeviceInfoVo vo = OrikaUtil.map(po, DeviceInfoVo.class);
            vo.setLoginTime(po.getCreateTime());
            vo.setIsLogin(0 == po.getDelFlag());
            vos.add(vo);
        }
        response.setDevices(vos);
        return response;
    }
}
