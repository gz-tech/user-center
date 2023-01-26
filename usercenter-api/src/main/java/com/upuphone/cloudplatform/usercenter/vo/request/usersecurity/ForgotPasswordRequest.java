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
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/22
 */
@ApiModel("忘记密码请求")
@Data
public class ForgotPasswordRequest {

    @ApiModelProperty(value = "userId")
    private String userId;

    @ApiModelProperty(value = "新密码", required = true)
    @Length(min = 8, max = 20, message = PWD_LEN_MSG)
    @Pattern(regexp = PWD_EXP, message = PWD_EXP_MSG)
    private String newPwd;
}
