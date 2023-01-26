package com.upuphone.cloudplatform.usercenter.controller.hellas;

import com.upuphone.cloudplatform.common.response.CommonResponse;
import com.upuphone.cloudplatform.usercenter.api.HellasClient;
import com.upuphone.cloudplatform.usercenter.service.authentication.CheckAccessTokenService;
import com.upuphone.cloudplatform.usercenter.service.hellas.CheckUserExistsByMobileService;
import com.upuphone.cloudplatform.usercenter.service.hellas.CreateLotusAccountService;
import com.upuphone.cloudplatform.usercenter.service.hellas.GenerateUpuTokenService;
import com.upuphone.cloudplatform.usercenter.service.hellas.LotusAccessTokenService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.logout.LogoutService;
import com.upuphone.cloudplatform.usercenter.service.userbasic.logout.model.LogoutReqVo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/11
 */
@RestController
public class HellasController implements HellasClient {

    @Autowired
    private GenerateUpuTokenService generateUpuTokenService;
    @Autowired
    private CheckAccessTokenService checkAccessTokenService;
    @Autowired
    private LotusAccessTokenService lotusAccessTokenService;
    @Autowired
    private CheckUserExistsByMobileService checkUserExistsByMobileService;
    @Autowired
    private CreateLotusAccountService createLotusAccountService;
    @Autowired
    private LogoutService logoutService;

    @Override
    public CommonResponse<TokenValidateResponse> validateToken(@Valid TokenValidateRequest request) {
        return CommonResponse.success(lotusAccessTokenService.process(request));
    }

    @Override
    public CommonResponse<CheckUserExistsByMobileResponse> checkUserExistsByMobile(@Valid CheckUserExistsByMobileRequest request) {
        return CommonResponse.success(checkUserExistsByMobileService.process(request));
    }

    @Override
    public CommonResponse<CreateAccountIfAbsentResponse> createLotusAccount(@Valid CreateAccountIfAbsentRequest request) {
        return CommonResponse.success(createLotusAccountService.process(request));
    }

    @Override
    public CommonResponse<GenerateUpuTokenResponse> generateToken(@Valid GenerateUpuTokenRequest request) {
        return CommonResponse.success(generateUpuTokenService.process(request));
    }

    @Override
    public CommonResponse<HellasLogoutResponse> hellasLogout(HellasLogoutRequest request) {
        LogoutReqVo logoutReqVo = new LogoutReqVo();
        logoutReqVo.setLogoutType(LogoutReqVo.LogoutType.HELLAS);
        logoutService.process(logoutReqVo);
        return CommonResponse.success(new HellasLogoutResponse());
    }
}
