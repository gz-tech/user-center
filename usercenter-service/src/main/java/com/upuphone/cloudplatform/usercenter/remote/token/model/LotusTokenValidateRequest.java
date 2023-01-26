package com.upuphone.cloudplatform.usercenter.remote.token.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/10
 */
@Setter
@Getter
public class LotusTokenValidateRequest implements TokenValidateRequest {

    private String accessToken;
    private String userId;
    private String mobile;

    @Override
    public String getSource() {
        return "lotus";
    }
}
