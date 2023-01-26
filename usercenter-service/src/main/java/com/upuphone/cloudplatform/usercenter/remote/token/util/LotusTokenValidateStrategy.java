package com.upuphone.cloudplatform.usercenter.remote.token.util;

import com.upuphone.cloudplatform.common.exception.RemoteException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.remote.token.model.LotusTokenValidateRemoteRequest;
import com.upuphone.cloudplatform.usercenter.remote.token.model.LotusTokenValidateRemoteResponse;
import com.upuphone.cloudplatform.usercenter.remote.token.model.LotusTokenValidateRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/10
 */
@Component
@Slf4j
public class LotusTokenValidateStrategy implements TokenValidateStrategy<LotusTokenValidateRequest, LotusTokenValidateRemoteResponse> {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${token.validate.lotus.url}")
    private String tokenValidateUrl;

    @Value("${token.validate.lotus.phone.url}")
    private String tokenAndPhoneValidateUrl;
//    public LotusTokenValidateRemoteResponse getResultFromRemote(LotusTokenValidateRequest request) {
//        // 可以拼装第三方需要的
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("token", request.getAccessToken());
//        ResponseEntity<LotusTokenValidateRemoteResponse> responseEntity =
//                restTemplate.exchange(tokenValidateUrl,
//                        HttpMethod.POST,
//                        new HttpEntity<>(new LotusTokenValidateRemoteRequest(), headers), LotusTokenValidateRemoteResponse.class);
//        log.info("response=================>{}", JsonUtility.toJson(responseEntity.getBody()));
//        // 校验response...
//        LotusTokenValidateRemoteResponse body = responseEntity.getBody();
//        if (null == body || !Objects.equals(body.getCode(), 10000000) || null == body.getData()) {
//            log.error("请求lotus校验token失败, body={}", JsonUtility.toJson(body));
//            throw new RemoteException(CommonErrorCode.REMOTE_ERROR, "请求lotus校验token失败");
//        }
//        body.setOriginId(request.getUserId());
//        return body;
//    }
    @Override
    @SneakyThrows
    public LotusTokenValidateRemoteResponse getResultFromRemote(LotusTokenValidateRequest request) {
        // 可以拼装第三方需要的
        HttpHeaders headers = new HttpHeaders();
        headers.set("token", request.getAccessToken());
        String url = String.format(tokenAndPhoneValidateUrl, request.getAccessToken(), request.getMobile());
        log.info("LotusTokenValidate request=================>{}", url);
        ResponseEntity<LotusTokenValidateRemoteResponse> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, new HttpEntity<>(new LotusTokenValidateRemoteRequest(), headers),
                        LotusTokenValidateRemoteResponse.class);
        log.info("response=================>{}", JsonUtility.toJson(responseEntity.getBody()));
        // 校验response...
        LotusTokenValidateRemoteResponse body = responseEntity.getBody();
        if (null == body || !Objects.equals(body.getCode(), 10000000) || null == body.getData()) {
            log.error("请求lotus校验token失败, body={}", JsonUtility.toJson(body));
            throw new RemoteException(CommonErrorCode.REMOTE_ERROR, "请求lotus校验token失败");
        }
        body.setOriginId(request.getUserId());
        return body;
    }
}
