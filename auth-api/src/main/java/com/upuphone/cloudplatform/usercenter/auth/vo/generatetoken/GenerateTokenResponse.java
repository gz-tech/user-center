package com.upuphone.cloudplatform.usercenter.auth.vo.generatetoken;

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
@ApiModel("生成Token response")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTokenResponse {

    @ApiModelProperty("refreshToken")
    private String refreshToken;

    @ApiModelProperty("accessToken")
    private String accessToken;

    @ApiModelProperty("accessToken失效时间（秒）")
    private Integer expiresIn;

    @ApiModelProperty(value = "授权域，逗号分隔", hidden = true)
    // 暂时没想好方案
    private String scope;
}
