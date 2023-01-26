package com.upuphone.cloudplatform.usercenter.remote.token.model;

import com.upuphone.cloudplatform.usercenter.entity.LotusUserInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/10
 */
@Getter
@Setter
public class LotusTokenValidateRemoteResponse implements TokenValidateRemoteResponse {

    private Integer code;

    private String message;

    private LotusUserInfo data;

    /**
     * 原ID 非远程请求返回
     */
    private String originId;

    @Override
    public Boolean isValid() {
        return null != this.data && Objects.equals(this.data.getLotusId(), this.originId);
    }

    @Override
    public String getUserId() {
        return this.data == null ? null : this.data.getLotusId();
    }
}
