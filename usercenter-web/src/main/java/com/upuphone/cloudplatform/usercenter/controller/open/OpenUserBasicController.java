package com.upuphone.cloudplatform.usercenter.controller.open;

import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.api.open.userbasic.OpenUserBasicClient;
import com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.request.UserDeviceRequest;
import com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.request.UserInfoRequest;
import com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.response.UserDeviceResponse;
import com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.response.UserInfoResponse;
import com.upuphone.cloudplatform.usercenter.service.open.UserDeviceService;
import com.upuphone.cloudplatform.usercenter.service.open.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class OpenUserBasicController implements OpenUserBasicClient {

    @Autowired
    private UserDeviceService userDeviceService;
    @Autowired
    private UserInfoService userInfoService;

    @Override
    public CommonResponse<UserDeviceResponse> userDevice(UserDeviceRequest request) {
        return CommonResponse.success(userDeviceService.process(request));
    }

    @Override
    public CommonResponse<UserInfoResponse> userInfo(UserInfoRequest request) {
        return CommonResponse.success(userInfoService.process(request));
    }
}
