package com.upuphone.cloudplatform.usercenter.remote.token.util;

import com.upuphone.cloudplatform.usercenter.remote.token.model.TokenValidateRemoteResponse;
import com.upuphone.cloudplatform.usercenter.remote.token.model.TokenValidateRequest;

public interface TokenValidateStrategy<RQ extends TokenValidateRequest, RP extends TokenValidateRemoteResponse> {

    RP getResultFromRemote(RQ request);
}
