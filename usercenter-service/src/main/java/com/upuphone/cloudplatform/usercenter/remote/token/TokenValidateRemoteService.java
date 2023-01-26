package com.upuphone.cloudplatform.usercenter.remote.token;

import com.upuphone.cloudplatform.common.component.BaseRemoteService;
import com.upuphone.cloudplatform.common.utils.SpringUtil;
import com.upuphone.cloudplatform.usercenter.remote.token.model.TokenValidateRemoteResponse;
import com.upuphone.cloudplatform.usercenter.remote.token.model.TokenValidateRequest;
import com.upuphone.cloudplatform.usercenter.remote.token.util.TokenValidateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/10
 */
@Service
@Slf4j
public class TokenValidateRemoteService extends BaseRemoteService<TokenValidateRequest, Boolean, TokenValidateRemoteResponse> {

    public TokenValidateRemoteService(@Value("unknown") String service, @Value("validate-token") String apiName) {
        super(service, apiName);
    }

    @Override
    protected Boolean fromRemoteResponse(TokenValidateRemoteResponse tokenValidateRemoteResponse) {
        return tokenValidateRemoteResponse.isValid();
    }

    @Override
    protected TokenValidateRemoteResponse processCore(TokenValidateRequest tokenValidateRequest) throws Exception {
        return this.getStrategy(tokenValidateRequest).getResultFromRemote(tokenValidateRequest);
    }

    @Override
    protected String getServiceName() {
        return "unknown";
    }

    @Override
    protected String getApiName() {
        return "validate-token";
    }

    @SuppressWarnings("unchecked")
    private TokenValidateStrategy<TokenValidateRequest, TokenValidateRemoteResponse> getStrategy(TokenValidateRequest request) {
        return (TokenValidateStrategy<TokenValidateRequest, TokenValidateRemoteResponse>)
                SpringUtil.getBean(request.getSource() + "TokenValidateStrategy");
    }
}
