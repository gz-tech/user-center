package com.upuphone.cloudplatform.usercenter.remote.email.model;

import com.upuphone.cloudplatform.usercenter.remote.RemoteResponse;
import lombok.Builder;
import lombok.Data;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/3
 */
@Data
@Builder
public class SendEmailResponse implements RemoteResponse {

    private Boolean status;

    public boolean isSuccess() {
        return Boolean.TRUE.equals(status);
    }
}
