package com.upuphone.cloudplatform.usercenter.auth.vo.authcode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/26
 */
@ApiModel("获取授权码response")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class GenerateAuthCodeResponse {

    @ApiModelProperty("回调地址 原样返回")
    private String redirectUri;

    @ApiModelProperty("state 原样返回")
    private String state;

    @ApiModelProperty("授权码")
    private String code;
}
