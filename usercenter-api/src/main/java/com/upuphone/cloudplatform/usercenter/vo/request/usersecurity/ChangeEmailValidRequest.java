package com.upuphone.cloudplatform.usercenter.vo.request.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/3
 */
@Data
@ApiModel("修改邮箱验证请求")
public class ChangeEmailValidRequest {

    @ApiModelProperty(value = "验证类型 1-安全手机号 2-绑定手机号 3-邮箱", required = true, allowableValues = "1,2,3")
    @NotNull
    @Range(min = 1, max = 3)
    private Integer validType;

    @ApiModelProperty(value = "验证码", required = true)
    @NotBlank
    private String validCode;
}
