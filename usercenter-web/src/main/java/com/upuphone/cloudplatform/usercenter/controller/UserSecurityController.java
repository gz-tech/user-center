package com.upuphone.cloudplatform.usercenter.controller;

import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.api.UserSecurityClient;
import com.upuphone.cloudplatform.usercenter.service.captcha.EnvironmentDetectionService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.BindSafeMobileService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.BindSafeMobileValidateService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ChangeEmailService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ChangeEmailValidateService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ChangeMobileService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ChangeMobileValidateService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ChangeSafeMobileService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ChangeSafeMobileValidateService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ForgotPasswordLoggedInService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ForgotPasswordService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ForgotPasswordValidateLoggedInService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ForgotPasswordValidateService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.GetMobileAndEmailLoggedInService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.GetMobileAndEmailService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.RelationDeviceListService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.RemoveDeviceService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ResetPasswordService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ResetPasswordValidateService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ValidatePasswordService;
import com.upuphone.cloudplatform.usercenter.service.usersecurity.ValidateValidCodeService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/23
 */
@RestController
public class UserSecurityController implements UserSecurityClient {

    @Autowired
    private ResetPasswordService resetPasswordService;
    @Autowired
    private GetMobileAndEmailService getMobileAndEmailService;
    @Autowired
    private ChangeSafeMobileValidateService changeSafeMobileValidateService;
    @Autowired
    private ChangeSafeMobileService changeSafeMobileService;
    @Autowired
    private BindSafeMobileValidateService bindSafeMobileValidateService;
    @Autowired
    private BindSafeMobileService bindSafeMobileService;
    @Autowired
    private ResetPasswordValidateService resetPasswordValidateService;
    @Autowired
    private ChangeMobileValidateService changeMobileValidateService;
    @Autowired
    private ChangeMobileService changeMobileService;
    @Autowired
    private ForgotPasswordValidateService forgotPasswordValidateService;
    @Autowired
    private ForgotPasswordService forgotPasswordService;
    @Autowired
    private RelationDeviceListService relationDeviceListService;
    @Autowired
    private RemoveDeviceService removeDeviceService;
    @Autowired
    private ChangeEmailValidateService changeEmailValidateService;
    @Autowired
    private ChangeEmailService changeEmailService;
    @Autowired
    private ForgotPasswordLoggedInService forgotPasswordLoggedInService;
    @Autowired
    private ForgotPasswordValidateLoggedInService forgotPasswordValidateLoggedInService;
    @Autowired
    private GetMobileAndEmailLoggedInService getMobileAndEmailLoggedInService;
    @Autowired
    private ValidatePasswordService validatePasswordService;
    @Autowired
    private ValidateValidCodeService validateValidCodeService;
    @Autowired
    private EnvironmentDetectionService environmentDetectionService;

    @Override
    public CommonResponse<ForgotPasswordValidateResponse> forgotPasswordValidate(@Valid ForgotPasswordValidateRequest request) {
        return CommonResponse.success(forgotPasswordValidateService.process(request));
    }

    @Override
    public CommonResponse<Void> forgotPassword(@Valid ForgotPasswordRequest request) {
        forgotPasswordService.process(request);
        return CommonResponse.success();
    }

    @Override
    public CommonResponse<SessionTicketResponse> resetPasswordValidate(@Valid ResetPasswordValidRequest request) {
        return CommonResponse.success(resetPasswordValidateService.process(request));
    }

    @Override
    public CommonResponse<Void> resetPassword(@Valid ResetPasswordRequest request) {
        resetPasswordService.process(request);
        return CommonResponse.success();
    }

    @Override
    public CommonResponse<SessionTicketResponse> changeMobileValidate(@Valid ChangeMobileValidateRequest request) {
        return CommonResponse.success(changeMobileValidateService.process(request));
    }

    @Override
    public CommonResponse<NewMobileResponse> changeMobile(@Valid ChangeMobileRequest request) {
        return CommonResponse.success(changeMobileService.process(request));
    }

    @Override
    public CommonResponse<RelationDeviceListResponse> getRelateDeviceList() {
        return CommonResponse.success(relationDeviceListService.process());
    }

    @Override
    public CommonResponse<RelationDeviceResponse> getRelateDeviceById(@Valid RelationDeviceRequest request) {
        return null;
    }

    @Override
    public CommonResponse<SessionTicketResponse> bindSafeMobileValidate(@Valid BindSafeMobileValidateRequest request) {
        return CommonResponse.success(bindSafeMobileValidateService.process(request));
    }

    @Override
    public CommonResponse<NewMobileResponse> bindSafeMobile(@Valid BindSafeMobileRequest request) {
        return CommonResponse.success(bindSafeMobileService.process(request));
    }

    @Override
    public CommonResponse<SessionTicketResponse> changeSafeMobileValidate(@Valid ChangeSafeMobileValidateRequest request) {
        return CommonResponse.success(changeSafeMobileValidateService.process(request));
    }

    @Override
    public CommonResponse<NewMobileResponse> changeSafeMobile(@Valid ChangeSafeMobileRequest request) {
        return CommonResponse.success(changeSafeMobileService.process(request));
    }

    @Override
    public CommonResponse<MobileAndEmailResponse> getMobileAndEmail(@Valid MobileAndEmailRequest request) {
        return CommonResponse.success(getMobileAndEmailService.process(request));
    }

    @Override
    public CommonResponse<Void> removeRelateDevice(@Valid RemoveDeviceRequest request) {
        removeDeviceService.process(request);
        return CommonResponse.success();
    }

    @Override
    public CommonResponse<SessionTicketResponse> changeEmailValidate(@Valid ChangeEmailValidRequest request) {
        return CommonResponse.success(changeEmailValidateService.process(request));
    }

    @Override
    public CommonResponse<NewEmailResponse> changeEmail(@Valid ChangeEmailRequest request) {
        return CommonResponse.success(changeEmailService.process(request));
    }

    @Override
    public CommonResponse<ForgotPasswordValidateResponse> forgotPasswordValidateLoggedIn(@Valid ForgotPasswordValidateRequest request) {
        return CommonResponse.success(forgotPasswordValidateLoggedInService.process(request));
    }

    @Override
    public CommonResponse<Void> forgotPasswordLoggedIn(@Valid ForgotPasswordRequest request) {
        return CommonResponse.success(forgotPasswordLoggedInService.process(request));
    }

    @Override
    public CommonResponse<MobileAndEmailResponse> getMobileAndEmailLoggedIn() {
        return CommonResponse.success(getMobileAndEmailLoggedInService.process());
    }

    @Override
    public CommonResponse<Void> validatePassword(@Valid ValidatePasswordRequest request) {
        validatePasswordService.process(request);
        return CommonResponse.success();
    }

    @Override
    public CommonResponse<Void> validateValidCode(@Valid ValidateValidCodeRequest request) {
        validateValidCodeService.process(request);
        return CommonResponse.success();
    }

    @Override
    public CommonResponse<EnvironmentDetectionResponse> environmentDetection(@RequestBody EnvironmentDetectionRequest request) {
        return CommonResponse.success(environmentDetectionService.process(request));
    }
}
