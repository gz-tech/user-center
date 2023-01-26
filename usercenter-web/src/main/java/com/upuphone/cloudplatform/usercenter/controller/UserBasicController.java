package com.upuphone.cloudplatform.usercenter.controller;

import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.api.UserBasicClient;
import com.upuphone.cloudplatform.usercenter.service.userbasic.DeleteAccountService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.UpdateUserDetailService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.UserDetailService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.FlashLoginService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.LoginByThirdPartyService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.LoginUsePasswordService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.LoginUseValidCodeService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.logout.LogoutService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.logout.model.LogoutReqVo;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.SimpleRegisterLoginService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.AccountBindingListService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.AccountBindingService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.thirdparty.AccountUnBindingService;
import com.upuphone.cloudplatform.usercenter.vo.request.DeleteAccountRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.UpdateUserInfoRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.AccountBindingRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.AccountUnbindingRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.login.FlashLoginRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUsePasswordRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.login.LoginUseValidCodeRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.logout.LogoutRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.logout.WebLogoutRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.register.SimpleRegisterLoginRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.DeleteAccountResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.GetUserDetailResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.UpdateUserInfoResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.AccountBindingListResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.AccountBindingResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.AccountUnbindingResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.login.LoginResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.logout.LogoutResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.logout.WebLogoutResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserBasicController implements UserBasicClient {

    @Autowired
    private LoginUseValidCodeService loginUseValidCodeService;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private UpdateUserDetailService updateUserDetailService;

    @Autowired
    private DeleteAccountService deleteAccountService;

    @Autowired
    private LogoutService logoutService;

    @Autowired
    private FlashLoginService flashLoginService;

    @Autowired
    private LoginUsePasswordService loginUsePasswordService;

    @Autowired
    private AccountBindingListService accountBindingListService;

    @Autowired
    private AccountUnBindingService accountUnBindingService;

    @Autowired
    private AccountBindingService accountBindingService;

    @Autowired
    private LoginByThirdPartyService loginByThirdPartyService;

    @Autowired
    private SimpleRegisterLoginService simpleRegisterLoginService;

    @Override
    public CommonResponse<LogoutResponse> logout(@Valid LogoutRequest request) {
        LogoutReqVo logoutReqVo = new LogoutReqVo();
        logoutReqVo.setLogoutType(LogoutReqVo.LogoutType.APP);
        logoutReqVo.setPassword(request.getPassword());
        logoutService.process(logoutReqVo);
        return CommonResponse.success(new LogoutResponse());
    }

    @Override
    public CommonResponse<WebLogoutResponse> webLogout(WebLogoutRequest request) {
        LogoutReqVo logoutReqVo = new LogoutReqVo();
        logoutReqVo.setLogoutType(LogoutReqVo.LogoutType.WEB);
        logoutService.process(logoutReqVo);
        return CommonResponse.success(new WebLogoutResponse());
    }

    @Override
    public CommonResponse<DeleteAccountResponse> deleteAccount(@Valid DeleteAccountRequest request) {
        deleteAccountService.process(request);
        return CommonResponse.success();
    }

    @Override
    public CommonResponse<LoginResponse> loginUseValidCode(@Valid LoginUseValidCodeRequest request) {
        return CommonResponse.success(loginUseValidCodeService.process(request));
    }

    @Override
    public CommonResponse<LoginResponse> loginUsePassword(@Valid LoginUsePasswordRequest request) {
        return CommonResponse.success(loginUsePasswordService.process(request));
    }


    @Override
    public CommonResponse<LoginResponse> flashLogin(@Valid FlashLoginRequest request) {
        LoginResponse loginResponse = flashLoginService.process(request);
        return new CommonResponse<>(loginResponse.getErrorCode(), loginResponse);
    }

    @Override
    public CommonResponse<GetUserDetailResponse> userDetail() {

        GetUserDetailResponse userDetailResponse = userDetailService.process();

        return CommonResponse.success(userDetailResponse);
    }

    @Override
    public CommonResponse<UpdateUserInfoResponse> updateUserInfo(@Valid UpdateUserInfoRequest request) {

        UpdateUserInfoResponse response = updateUserDetailService.process(request);

        return CommonResponse.success(response);
    }

    @Override
    public CommonResponse<AccountBindingListResponse> accountBindingList() {
        return CommonResponse.success(accountBindingListService.process(null));
    }

    @Override
    public CommonResponse<AccountBindingResponse> accountBinding(AccountBindingRequest request) {
        return CommonResponse.success(accountBindingService.process(request));
    }

    @Override
    public CommonResponse<AccountUnbindingResponse> accountUnbinding(AccountUnbindingRequest request) {
        return CommonResponse.success(accountUnBindingService.process(request));
    }

    @Override
    public CommonResponse<LoginResponse> thirdPartLogin(AccountBindingRequest request) {
        return CommonResponse.success(loginByThirdPartyService.process(request));
    }

    @Override
    public CommonResponse<LoginResponse> simpleRegisterLogin(SimpleRegisterLoginRequest request) {
        return CommonResponse.success(simpleRegisterLoginService.process(request));
    }
}
