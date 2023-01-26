package com.upuphone.cloudplatform.usercenter.vo.request;

import com.upuphone.cloudplatform.usercenter.vo.UserAccountInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/11
 */
@ApiModel("创建lotus帐户 request")
@Data
public class CreateAccountIfAbsentRequest {

    @ApiModelProperty(value = "lotusId", required = true)
    @NotBlank(message = "lotusId不能为空")
    private String lotusId;
    @ApiModelProperty(value = "lotus accessToken", required = true)
    @NotBlank(message = "lotus accessToken不能为空")
    private String lotusAccessToken;
    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    private String mobile;
    @ApiModelProperty(value = "用户信息", required = true)
    @NotNull(message = "userInfo不能为空")
    private UserAccountInfo userInfo;
}
