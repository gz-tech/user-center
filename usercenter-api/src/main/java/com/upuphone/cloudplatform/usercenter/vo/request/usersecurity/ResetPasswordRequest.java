package com.upuphone.cloudplatform.usercenter.vo.request.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.PWD_EXP;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.PWD_EXP_MSG;
import static com.upuphone.cloudplatform.usercenter.constants.ApiConstants.PWD_LEN_MSG;

/**
 * Description: 修改密码请求
 *
 * @author hanzhumeng
 * Created: 2021/12/22
 */
@Data
@ApiModel("修改密码请求")
public class ResetPasswordRequest {

    @ApiModelProperty(value = "新密码", required = true)
    @Length(min = 8, max = 20, message = PWD_LEN_MSG)
    @Pattern(regexp = PWD_EXP, message = PWD_EXP_MSG)
    private String newPwd;
}
