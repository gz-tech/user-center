package com.upuphone.cloudplatform.usercenter.auth.vo.refresh;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/14
 */
@Data
@ApiModel("refreshToken Response")
public class RefreshTokenResponse {

    @ApiModelProperty("新的accessToken")
    private String accessToken;

    @ApiModelProperty("accessToken截止时间戳")
    private Long accessTokenExpiresAt;

    @ApiModelProperty("新的refreshToken")
    private String refreshToken;

    @ApiModelProperty("refreshToken截止时间戳")
    private Long refreshTokenExpiresAt;
}
