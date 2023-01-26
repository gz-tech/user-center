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
@ApiModel("生成星纪token request")
@Data
public class GenerateUpuTokenRequest {

    @ApiModelProperty(value = "lotusId", required = true)
    @NotBlank(message = "lotusId不能为空")
    private String lotusId;
    @ApiModelProperty(value = "lotus accessToken", required = true)
    @NotBlank(message = "lotus accessToken不能为空")
    private String lotusAccessToken;
    @ApiModelProperty(value = "星纪userId", required = true)
    private String userId;

    @ApiModelProperty(value = "手机号", required = true)
    private String mobile;
}
