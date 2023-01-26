package com.upuphone.cloudplatform.usercenter.controller;

import com.upuphone.cloudplatform.baseinfo.vo.country.request.QueryCountryRequest;
import com.upuphone.cloudplatform.baseinfo.vo.country.response.QueryCountryResponse;
import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.api.BasicClient;
import com.upuphone.cloudplatform.usercenter.common.util.OrikaUtil;
import com.upuphone.cloudplatform.usercenter.remote.basic.QueryCountryService;
import com.upuphone.cloudplatform.usercenter.service.basic.EmailSendValidCodeService;
import com.upuphone.cloudplatform.usercenter.service.basic.EmailSendValidCodeWithoutAddressService;
import com.upuphone.cloudplatform.usercenter.service.basic.GetPhoneAreaCodeService;
import com.upuphone.cloudplatform.usercenter.service.basic.JudgeOauthAppService;
import com.upuphone.cloudplatform.usercenter.service.basic.RegisterModeService;
import com.upuphone.cloudplatform.usercenter.service.basic.SendValidCodeService;
import com.upuphone.cloudplatform.usercenter.service.basic.SendValidCodeWithoutPhoneService;
import com.upuphone.cloudplatform.usercenter.service.basic.UploadPhotoService;
import com.upuphone.cloudplatform.usercenter.vo.CountryVo;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.*;
import com.upuphone.cloudplatform.usercenter.vo.response.basic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class BasicController implements BasicClient {

    @Autowired
    private EmailSendValidCodeService emailSendValidCodeService;
    @Autowired
    private EmailSendValidCodeWithoutAddressService emailSendValidCodeWithoutAddressService;
    @Autowired
    private SendValidCodeService sendValidCodeService;
    @Autowired
    private GetPhoneAreaCodeService getPhoneAreaCodeService;
    @Autowired
    private SendValidCodeWithoutPhoneService sendValidCodeWithoutPhoneService;
    @Autowired
    private UploadPhotoService uploadPhotoService;
    @Autowired
    private QueryCountryService queryCountryService;
    @Autowired
    private JudgeOauthAppService judgeOauthAppService;
    @Autowired
    private RegisterModeService registerModeService;

    @Override
    public CommonResponse<EmailSendValidCodeResponse> emailSendValidCode(EmailSendValidCodeRequest request) {
        return CommonResponse.success(emailSendValidCodeService.process(request));
    }

    @Override
    public CommonResponse<EmailSendValidCodeWithoutAddressResponse> emailSendValidCodeWithoutAddress(
            EmailSendValidCodeWithoutAddressRequest request) {
        return CommonResponse.success(emailSendValidCodeWithoutAddressService.process(request));
    }

    @Override
    public CommonResponse<SendValidCodeResponse> sendValidCode(SendValidCodeRequest request) {
        CommonResponse<SendValidCodeResponse> commonResponse = new CommonResponse<>();
        commonResponse.setData(sendValidCodeService.process(request));
        return commonResponse;
    }

    @Override
    public CommonResponse<Boolean> sendValidCodeWithoutPhone(SendValidCodeWithoutPhoneRequest request) {
        return CommonResponse.success(sendValidCodeWithoutPhoneService.process(request));
    }

    @Override
    public CommonResponse<PhoneAreaCodeResponse> phoneAreaCode() {
        PhoneAreaCodeResponse result = getPhoneAreaCodeService.process(null);
        return CommonResponse.success(result);
    }

    @Override
    public CommonResponse<UpLoadPhotoResponse> uploadPhoto(MultipartFile multipartFile) {

        UpLoadPhotoResponse result = uploadPhotoService.process(multipartFile);
        return CommonResponse.success(result);
    }

    @Override
    public CommonResponse<CountryListResponse> queryCountryList() {
        QueryCountryResponse result = queryCountryService.process(new QueryCountryRequest());
        CountryListResponse response = new CountryListResponse();
        response.setCountries(OrikaUtil.mapAsList(result.getCountries(), CountryVo.class));
        return CommonResponse.success(response);
    }

    @Override
    public CommonResponse<Boolean> isOauthAppId() {
        return CommonResponse.success(judgeOauthAppService.process(null));
    }

    @Override
    public CommonResponse<String> isSimpleRegister() {
        return CommonResponse.success(registerModeService.process(null));
    }

}
