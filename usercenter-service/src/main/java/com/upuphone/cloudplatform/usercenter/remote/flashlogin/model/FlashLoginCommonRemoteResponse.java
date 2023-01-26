package com.upuphone.cloudplatform.usercenter.remote.flashlogin.model;

import lombok.Data;

/**
 * Description:
 *
 * @param <T> type of response
 * @author hanzhumeng
 * Created: 2022/1/27
 */
@Data
public class FlashLoginCommonRemoteResponse<T> {

    private String code;

    private String message;

    private Integer chargeStatus;

    private T data;
}
