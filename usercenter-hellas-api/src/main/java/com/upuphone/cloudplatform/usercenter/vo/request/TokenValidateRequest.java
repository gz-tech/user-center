package com.upuphone.cloudplatform.usercenter.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/11
 */
@ApiModel("验证星纪token request")
@Data
public class TokenValidateRequest {

    @ApiModelProperty(value = "星纪accessToken", required = true)
    @NotBlank(message = "星纪accessToken不能为空")
    private String upuAccessToken;

    @ApiModelProperty(value = "用户手机")
    private String mobile;
}
