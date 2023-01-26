package com.upuphone.cloudplatform.usercenter.controller;

import com.upuphone.cloudplatform.common.context.RequestContext;
import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.api.UserRegistClient;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.CheckEmailCodeRegistService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.CheckPhoneCodeRegistService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.RegistService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.register.model.RegistReqBo;
import com.upuphone.cloudplatform.usercenter.vo.request.register.CheckEmailCodeForRegistRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.register.CheckPhoneCodeForRegistRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.register.EmailRegistRequest;
import com.upuphone.cloudplatform.usercenter.vo.request.register.PhoneRegistRequest;
import com.upuphone.cloudplatform.usercenter.vo.response.register.CheckEmailCodeForRegistResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.register.CheckPhoneCodeForRegistResponse;
import com.upuphone.cloudplatform.usercenter.vo.response.register.RegistResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserRegistController implements UserRegistClient {

    @Autowired
    private CheckPhoneCodeRegistService checkPhoneCodeRegistService;

    @Autowired
    private RegistService registService;


    @Autowired
    private CheckEmailCodeRegistService checkEmailCodeRegistService;

    @Override
    public CommonResponse<CheckPhoneCodeForRegistResponse> checkCodeForRegister(@Valid CheckPhoneCodeForRegistRequest request) {
        CommonResponse<CheckPhoneCodeForRegistResponse> commonResponse = new CommonResponse<>();
        CheckPhoneCodeForRegistResponse response = checkPhoneCodeRegistService.process(request);
        commonResponse.setData(response);
        return commonResponse;
    }

    @Override
    public CommonResponse<RegistResponse> register(@Valid PhoneRegistRequest request) {

        RegistReqBo registReqBo = new RegistReqBo();
        registReqBo.setPhoneCode(request.getPhoneCode());
        registReqBo.setPhoneNumber(request.getPhoneNumber());
        registReqBo.setPassword(request.getPassword());
        registReqBo.setTicket(RequestContext.getSessionTicket());
        registReqBo.setRegistType(RegistReqBo.RegistType.PHONE);
        registReqBo.setThirdPartAuthTicket(request.getThirdPartAuthTicket());
        RegistResponse result = registService.process(registReqBo);

        return CommonResponse.success(result);
    }

    @Override
    public CommonResponse<CheckEmailCodeForRegistResponse> checkEmailCodeForRegist(@Valid CheckEmailCodeForRegistRequest request) {
        CheckEmailCodeForRegistResponse response = checkEmailCodeRegistService.process(request);
        return CommonResponse.success(response);
    }

    @Override
    public CommonResponse<RegistResponse> emailRegist(@Valid EmailRegistRequest request) {

        RegistReqBo registReqBo = new RegistReqBo();
        registReqBo.setEmailAddress(request.getEmailAddress());
        registReqBo.setPassword(request.getPassword());
        registReqBo.setTicket(request.getEmailAddressCheckTicket());
        registReqBo.setRegistType(RegistReqBo.RegistType.EMAIL);
        registReqBo.setThirdPartAuthTicket(request.getThirdPartAuthTicket());
        RegistResponse result = registService.process(registReqBo);
        return CommonResponse.success(result);
    }
}
