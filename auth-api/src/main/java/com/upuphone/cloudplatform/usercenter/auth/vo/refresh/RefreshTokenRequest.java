package com.upuphone.cloudplatform.usercenter.auth.vo.refresh;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/14
 */
@Data
@ApiModel("刷新token请求")
public class RefreshTokenRequest {

    @ApiModelProperty(value = "refresh Token", required = true)
    @NotBlank(message = "refreshToken不能为空")
    private String refreshToken;
}
