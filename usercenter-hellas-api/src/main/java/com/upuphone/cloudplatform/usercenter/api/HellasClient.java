package com.upuphone.cloudplatform.usercenter.api;

import com.upuphone.cloudplatform.common.annotation.CheckHeader;
import com.upuphone.cloudplatform.common.annotation.CheckHeaders;
import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.vo.request.CheckUserExistsByMobileRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.CreateAccountIfAbsentRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.GenerateUpuTokenRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.HellasLogoutRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.HellasLogoutResponse;
import com.upuphone.cloudplatform.usercenter.vo.request.TokenValidateRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.CheckUserExistsByMobileResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.CreateAccountIfAbsentResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.GenerateUpuTokenResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.TokenValidateResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/10
 */
@Api(tags = "hellas API")
@FeignClient(name = "user-center", contextId = "hellas")
public interface HellasClient {

    /*
    * 1. 验证星纪TOKEN
2. 验证第三方TOKEN（非接口）
3. 星纪REFRESH TOKEN+DEVICEID刷新星纪TOKEN（已有？）
4. 手机号判断用户是否存在 返回星纪ID
5. 接收lotusID+token+用户信息创建用户 返回星纪ID
6. 接收lotusID+星纪ID+lotusToken 返回星纪ID+星纪ACCESS+REFRESH
7. 星纪ID+DEVICEID 批量Logout(待定)*/

    @ApiOperation("验证星纪token")
    @PostMapping("/hellas/validate-token")
    CommonResponse<TokenValidateResponse> validateToken(@RequestBody TokenValidateRequest request);

    @ApiOperation("根据手机号检查用户是否存在")
    @PostMapping("/hellas/check-user-exists-by-mobile")
    CommonResponse<CheckUserExistsByMobileResponse> checkUserExistsByMobile(@RequestBody CheckUserExistsByMobileRequest request);

    @ApiOperation("根据用户信息查找或创建星纪用户, 返回星纪用户ID")
    @PostMapping("/hellas/create-account-if-absent")
    CommonResponse<CreateAccountIfAbsentResponse> createLotusAccount(@RequestBody CreateAccountIfAbsentRequest request);

    @ApiOperation("创建星纪AccessToken&RefreshToken")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-app-id", value = "appId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-model", value = "设备型号", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-type", value = "4-车机", required = true)
    })
    @CheckHeaders({
            @CheckHeader("X-app-id"),
            @CheckHeader("X-device-id"),
            @CheckHeader("X-device-type"),
            @CheckHeader("X-model")
    })
    @PostMapping("/hellas/generate-token")
    CommonResponse<GenerateUpuTokenResponse> generateToken(@RequestBody GenerateUpuTokenRequest request);

    @ApiOperation("web用户登出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId"),
            @ApiImplicitParam(paramType = "header", name = "X-refresh-token-id", value = "[前端不传]")
    })
    @PostMapping("/hellas-logout")
    CommonResponse<HellasLogoutResponse> hellasLogout(@RequestBody HellasLogoutRequest request);
}
