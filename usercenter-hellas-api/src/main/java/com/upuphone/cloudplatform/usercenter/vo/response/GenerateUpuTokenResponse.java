package com.upuphone.cloudplatform.usercenter.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/11
 */
@ApiModel("生成星纪token response")
@Data
public class GenerateUpuTokenResponse {

    @ApiModelProperty("星纪access Token")
    private String accessToken;
    @ApiModelProperty("accessToken截止时间戳")
    private Long accessTokenExpiresAt;
    @ApiModelProperty("星纪refresh Token")
    private String refreshToken;
    @ApiModelProperty("refreshToken截止时间戳")
    private Long refreshTokenExpiresAt;
    @ApiModelProperty("星纪userID")
    private String userId;
}
