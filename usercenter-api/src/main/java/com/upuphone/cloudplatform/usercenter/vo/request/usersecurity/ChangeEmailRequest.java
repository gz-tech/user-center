package com.upuphone.cloudplatform.usercenter.vo.request.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.EMAIL_EXP;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/3
 */
@Data
@ApiModel("修改邮箱请求")
public class ChangeEmailRequest {

    @ApiModelProperty(value = "新邮箱", required = true)
    @NotBlank
    @Pattern(regexp = EMAIL_EXP)
    private String newEmail;

    @ApiModelProperty(value = "验证码", required = true)
    @NotBlank
    private String validCode;
}
