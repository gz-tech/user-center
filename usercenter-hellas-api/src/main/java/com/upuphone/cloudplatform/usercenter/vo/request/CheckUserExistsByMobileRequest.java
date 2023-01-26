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
@ApiModel("根据手机号判断用户是否存在 request")
@Data
public class CheckUserExistsByMobileRequest {

    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    private String mobile;
    @ApiModelProperty(value = "lotusId", required = true)
    @NotBlank(message = "lotusId不能为空")
    private String lotusId;
    @ApiModelProperty(value = "lotus accessToken", required = true)
    @NotBlank(message = "lotusAccessToken不能为空")
    private String lotusAccessToken;
}
