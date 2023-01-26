package com.upuphone.cloudplatform.usercenter.api;

import com.upuphone.cloudplatform.common.annotation.CheckHeader;
import com.upuphone.cloudplatform.common.annotation.CheckHeaders;
import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.BindSafeMobileRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.BindSafeMobileValidateRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ChangeEmailRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ChangeEmailValidRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ChangeMobileRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ChangeMobileValidateRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ChangeSafeMobileRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ChangeSafeMobileValidateRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.EnvironmentDetectionRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ForgotPasswordRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ForgotPasswordValidateRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.MobileAndEmailRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.RelationDeviceRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.RemoveDeviceRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ResetPasswordRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ResetPasswordValidRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ValidatePasswordRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.usersecurity.ValidateValidCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.EnvironmentDetectionResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.ForgotPasswordValidateResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.MobileAndEmailResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.NewEmailResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.NewMobileResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.RelationDeviceListResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.RelationDeviceResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.usersecurity.SessionTicketResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(tags = "安全中心API")
@FeignClient(name = "user-center", contextId = "user-security")
public interface UserSecurityClient {

    /**
     * 找回密码[未登录态](step1-验证手机号)
     *
     * @param request ForgotPasswordValidateRequest
     * @return ForgotPasswordValidateResponse
     */
    @PostMapping("/security/password/forgot-validate")
    @ResponseBody
    @ApiOperation("[未登录]找回密码(step1-验证手机号)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true)
    })
    @CheckHeaders({
            @CheckHeader("X-device-id")
    })
    CommonResponse<ForgotPasswordValidateResponse> forgotPasswordValidate(@RequestBody ForgotPasswordValidateRequest request);

    /**
     * 找回密码[登录态](step1-验证手机号)
     *
     * @param request ForgotPasswordValidateRequest
     * @return ForgotPasswordValidateResponse
     */
    @PostMapping("/security/password/forgot-validate-logged-in")
    @ResponseBody
    @ApiOperation("[登录态]找回密码(step1-验证手机号)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-device-id"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<ForgotPasswordValidateResponse> forgotPasswordValidateLoggedIn(@RequestBody ForgotPasswordValidateRequest request);

    /**
     * 找回密码[未登录态](step2-通过密码重置密码)
     *
     * @param request ForgotPasswordRequest
     * @return ForgotPasswordValidateResponse
     */
    @PostMapping("/security/password/forgot")
    @ResponseBody
    @ApiOperation("[未登录]找回密码(step2-通过密码重置密码)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-session-ticket", value = "sessionTicket", required = true)
    })
    @CheckHeaders({
            @CheckHeader("X-session-ticket")
    })
    CommonResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request);

    /**
     * 找回密码[登录态](step2-通过密码重置密码)
     *
     * @param request ForgotPasswordRequest
     * @return ForgotPasswordValidateResponse
     */
    @PostMapping("/security/password/forgot-logged-in")
    @ResponseBody
    @ApiOperation("[登录态]找回密码(step2-通过密码重置密码)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-session-ticket", value = "sessionTicket", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-session-ticket"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<Void> forgotPasswordLoggedIn(@RequestBody ForgotPasswordRequest request);

    /**
     * 修改密码(step1-验证原密码)
     *
     * @param request ResetPasswordValidRequest
     * @return 持续5分钟的token 后续请求需要持有
     */
    @PostMapping("/security/password/reset-validate")
    @ApiOperation("修改密码(step1-验证原密码)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-device-id"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<SessionTicketResponse> resetPasswordValidate(@RequestBody ResetPasswordValidRequest request);

    /**
     * 修改密码(step2-通过密码重置密码)
     *
     * @param request ResetPasswordRequest
     * @return true
     */
    @PostMapping("/security/password/reset")
    @ResponseBody
    @ApiOperation("修改密码(step2-通过密码重置密码)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-session-ticket", value = "sessionTicket", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-session-ticket"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request);

    /**
     * 换绑手机号(step1-验证手机号)
     *
     * @param request ChangeMobileValidateRequest
     * @return 持续5分钟的token 后续请求需要持有
     */
    @PostMapping("/security/mobile/change-validate")
    @ResponseBody
    @ApiOperation("换绑手机号(step1-验证手机号)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-device-id"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<SessionTicketResponse> changeMobileValidate(@RequestBody ChangeMobileValidateRequest request);

    /**
     * 换绑手机号(step2-验证新手机号)
     *
     * @param request ChangeMobileRequest
     * @return 新手机号
     */
    @PostMapping("/security/mobile/change")
    @ResponseBody
    @ApiOperation("换绑手机号(step2-验证新手机号)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-session-ticket", value = "sessionTicket", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-session-ticket"),
            @CheckHeader("X-device-id"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<NewMobileResponse> changeMobile(@RequestBody ChangeMobileRequest request);

    /**
     * 设备管理-设备列表
     *
     * @return RelationDeviceListResponse
     */
    @GetMapping("/security/devices/list")
    @ResponseBody
    @ApiOperation("设备管理-设备列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-device-id"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<RelationDeviceListResponse> getRelateDeviceList();

    /**
     * 设备管理-移除设备
     *
     * @param request RemoveDeviceRequest
     * @return true
     */
    @PostMapping("/security/devices/remove")
    @ResponseBody
    @ApiOperation("设备管理-移除设备")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-device-id"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<Void> removeRelateDevice(@RequestBody RemoveDeviceRequest request);

    /**
     * todo 设备管理-设备详情 暂时不写
     *
     * @param request RelationDeviceRequest
     * @return RelationDeviceResponse
     */
    @ApiOperation(value = "设备管理-设备详情 暂时不写", hidden = true)
    CommonResponse<RelationDeviceResponse> getRelateDeviceById(@RequestBody RelationDeviceRequest request);

    /**
     * 设置安全手机号(step1-验证原密码)
     *
     * @param request BindSafeMobileValidateRequest
     * @return 持续5分钟的token 后续请求需要持有
     */
    @PostMapping("/security/safe-mobile/bind-validate")
    @ResponseBody
    @ApiOperation("设置安全手机号(step1-验证原密码)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-device-id"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<SessionTicketResponse> bindSafeMobileValidate(@RequestBody BindSafeMobileValidateRequest request);

    /**
     * 设置安全手机号(step2-验证新安全手机号)
     *
     * @param request BindSafeMobileRequest
     * @return 安全手机号
     */
    @PostMapping("/security/safe-mobile/bind")
    @ResponseBody
    @ApiOperation("设置安全手机号(step2-验证新安全手机号)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-session-ticket", value = "sessionTicket", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-session-ticket"),
            @CheckHeader("X-device-id"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<NewMobileResponse> bindSafeMobile(@RequestBody BindSafeMobileRequest request);

    /**
     * 修改安全手机号(step1-验证当前安全手机号)
     *
     * @param request ChangeSafeMobileValidateRequest
     * @return 持续5分钟的token 后续请求需要持有
     */
    @PostMapping("/security/safe-mobile/change-validate")
    @ResponseBody
    @ApiOperation("修改安全手机号(step1-验证当前安全手机号)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-device-id"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<SessionTicketResponse> changeSafeMobileValidate(@RequestBody ChangeSafeMobileValidateRequest request);

    /**
     * 修改安全手机号(step2-修改安全手机号)
     *
     * @param request ChangeSafeMobileRequest
     * @return 新安全手机号
     */
    @PostMapping("/security/safe-mobile/change")
    @ResponseBody
    @ApiOperation("修改安全手机号(step2-修改安全手机号)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-session-ticket", value = "sessionTicket", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-session-ticket"),
            @CheckHeader("X-device-id"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<NewMobileResponse> changeSafeMobile(@RequestBody ChangeSafeMobileRequest request);

    /**
     * [未登录]获取安全、绑定手机号和邮箱掩码
     *
     * @param request request
     * @return 手机号&邮箱掩码
     */
    @PostMapping("/security/mobile-and-email")
    @ResponseBody
    @ApiOperation("[未登录]获取手机号和邮箱掩码，可以判断是否存在手机号和邮箱")
    CommonResponse<MobileAndEmailResponse> getMobileAndEmail(@RequestBody MobileAndEmailRequest request);

    /**
     * [登录态]获取安全、绑定手机号和邮箱掩码
     *
     * @return 手机号&邮箱掩码
     */
    @PostMapping("/security/mobile-and-email-logged-in")
    @ResponseBody
    @ApiOperation("[登录态]获取手机号和邮箱掩码，可以判断是否存在手机号和邮箱")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<MobileAndEmailResponse> getMobileAndEmailLoggedIn();

    /**
     * 修改/绑定邮箱(step1-安全验证)
     *
     * @param request request
     * @return session token
     */
    @PostMapping("/security/email/change-validate")
    @ResponseBody
    @ApiOperation("修改/绑定邮箱(step1-安全验证)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-device-id"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<SessionTicketResponse> changeEmailValidate(@RequestBody ChangeEmailValidRequest request);

    /**
     * 修改/绑定邮箱(step2-输入新邮箱地址)
     *
     * @param request request
     * @return new email
     */
    @PostMapping("/security/email/change")
    @ResponseBody
    @ApiOperation("修改/绑定邮箱(step2-输入新邮箱地址)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-session-ticket", value = "sessionTicket", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader("X-session-ticket"),
            @CheckHeader("X-device-id"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<NewEmailResponse> changeEmail(@RequestBody ChangeEmailRequest request);

    @PostMapping("/security/password/validate")
    @ResponseBody
    @ApiOperation("[登录态]验证密码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId")
    })
    @CheckHeaders({
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<Void> validatePassword(@RequestBody ValidatePasswordRequest request);

    @PostMapping("/security/valid-code/validate")
    @ResponseBody
    @ApiOperation("[登录态]验证手机/邮箱验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-pub-key", value = "非对称公钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-res-aes-key", value = "公钥加密后的对称密钥", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken", required = true),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId"),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
    })
    @CheckHeaders({
            @CheckHeader("X-device-id"),
            @CheckHeader(value = "X-user-id", strategy = "userIdCheckHeaderStrategy", cls = Long.class)
    })
    CommonResponse<Void> validateValidCode(@RequestBody ValidateValidCodeRequest request);

    @PostMapping("/security/environment-detection")
    @ApiOperation("判断是否需要环境检测验证")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "X-access-token", value = "accessToken，登录态传，非登录态不传"),
            @ApiImplicitParam(paramType = "header", name = "X-user-id", value = "[前端不传] userId"),
            @ApiImplicitParam(paramType = "header", name = "X-device-id", value = "deviceId", required = true),
    })
    CommonResponse<EnvironmentDetectionResponse> environmentDetection(EnvironmentDetectionRequest request);
}
