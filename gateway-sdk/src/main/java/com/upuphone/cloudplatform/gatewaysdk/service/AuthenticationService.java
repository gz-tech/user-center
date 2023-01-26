package com.upuphone.cloudplatform.gatewaysdk.service;

import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.gatewaysdk.pojo.TokenHeaderBO;
import reactor.core.publisher.Mono;

public interface AuthenticationService {

    /**
     * @param token
     * @return
     * @throws BusinessException
     */
    Mono<TokenHeaderBO> processToken(String token);
}
