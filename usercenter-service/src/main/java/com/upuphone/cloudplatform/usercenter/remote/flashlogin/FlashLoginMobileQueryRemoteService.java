package com.upuphone.cloudplatform.usercenter.remote.flashlogin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.upuphone.cloudplatform.common.component.BaseRemoteService;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.remote.flashlogin.model.FlashLoginCommonRemoteRequest;
import com.upuphone.cloudplatform.usercenter.remote.flashlogin.model.FlashLoginCommonRemoteResponse;
import com.upuphone.cloudplatform.usercenter.remote.flashlogin.model.FlashLoginMobileQueryRequest;
import com.upuphone.cloudplatform.usercenter.remote.flashlogin.model.FlashLoginMobileQueryResponse;
import com.upuphone.cloudplatform.usercenter.remote.flashlogin.model.MobileQueryResponse;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.util.AESUtil;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.util.MD5Util;
import com.upuphone.cloudplatform.usercenter.service.userbasic.login.util.SignUtil;
import com.upuphone.cloudplatform.usercenter.setting.Setting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/27
 */

@Service
@Slf4j
@SuppressWarnings("unchecked")
public class FlashLoginMobileQueryRemoteService extends BaseRemoteService<FlashLoginMobileQueryRequest,
        FlashLoginMobileQueryResponse, MobileQueryResponse> {

    @Autowired
    private Setting setting;

    @Autowired
    private OkHttpPostService okHttpPostService;

    public FlashLoginMobileQueryRemoteService(@Value("flashLogin") String service, @Value("mobileQuery") String apiName) {
        super(service, apiName);
    }

    @Override
    protected FlashLoginMobileQueryResponse fromRemoteResponse(MobileQueryResponse remoteResponse) {
        String mobile = remoteResponse.getMobileName();
        String key = MD5Util.getMD5Code(setting.getAppKey());
        try {
            mobile = AESUtil.decrypt(mobile, key.substring(0, 16), key.substring(16));
            FlashLoginMobileQueryResponse response = new FlashLoginMobileQueryResponse();
            response.setMobile(mobile);
            return response;
        } catch (Exception e) {
            log.error("[FlashLoginMobileQueryRemoteService] ??????mobile????????? mobile=[{}]", mobile, e);
            return null;
        }
    }

    @Override
    protected MobileQueryResponse processCore(FlashLoginMobileQueryRequest request) throws Exception {
        String token = request.getToken();
        FlashLoginCommonRemoteRequest commonRemoteRequest = new FlashLoginCommonRemoteRequest();
        commonRemoteRequest.setAppId(setting.getAppId());
        commonRemoteRequest.setToken(token);
        Map<String, String> params = (Map<String, String>) JsonUtility.objectToMap(commonRemoteRequest);
        params.put("sign", SignUtil.getSign(params, setting.getAppKey()));
        try {
            log.info("[get 253 token]params, {}", JsonUtility.toJson(params));
            FlashLoginCommonRemoteResponse<MobileQueryResponse> remoteResponse =
                    okHttpPostService.postRequest(setting.getFlashLoginUrl(),
                            params,
                            new TypeReference<FlashLoginCommonRemoteResponse<MobileQueryResponse>>() {
                            });
            if (null == remoteResponse) {
                log.error("???????????????????????????????????????, params={}", JsonUtility.toJson(params));
                throw new BusinessException(CommonErrorCode.SERVICE_ERROR, "???????????????????????????????????????");
            }
            log.info("[get 253 token]response:{}", JsonUtility.toJson(remoteResponse));
            //????????? 200000?????????
            String code = remoteResponse.getCode();
            if (!"200000".equals(code)) {
                log.error("?????????????????????, code != 200000, {}", remoteResponse.getMessage());
                throw new BusinessException(CommonErrorCode.SERVICE_ERROR, "???????????????????????????????????????");
            }
            return remoteResponse.getData();
        } catch (Exception e) {
            log.error("?????????????????????, e={}", e.getMessage());
            throw new BusinessException(CommonErrorCode.SERVICE_ERROR, "?????????????????????");
        }
    }

    @Override
    protected String getServiceName() {
        return "flashLogin";
    }

    @Override
    protected String getApiName() {
        return "mobileQuery";
    }
}
