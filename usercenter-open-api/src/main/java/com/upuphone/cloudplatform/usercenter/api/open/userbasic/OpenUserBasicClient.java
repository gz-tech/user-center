package com.upuphone.cloudplatform.usercenter.api.open.userbasic;


import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.request.UserDeviceRequest;
import com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.request.UserInfoRequest;
import com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.response.UserDeviceResponse;
import com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.response.UserInfoResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(tags = "【开放接口】【非客户端使用接口】用户基础功能 API")
@FeignClient(name = "user-center", contextId = "open-api")
public interface OpenUserBasicClient {


    @ApiOperation("查询用户设备信息")
    @GetMapping("/open/user-basic/user-device")
    @ResponseBody
    CommonResponse<UserDeviceResponse> userDevice(@SpringQueryMap UserDeviceRequest request);

    @ApiOperation("查询用户信息(支持批量)")
    @PostMapping("/open/user-basic/user-info")
    @ResponseBody
    CommonResponse<UserInfoResponse> userInfo(@RequestBody UserInfoRequest request);
}
