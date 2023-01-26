package com.upuphone.cloudplatform.usercenter.vo.response.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/22
 */
@Data
@ApiModel(value = "找回密码response")
public class ForgotPasswordValidateResponse {

    @ApiModelProperty(value = "sessionTicket")
    private String sessionTicket;

    @ApiModelProperty(value = "userID")
    private String userId;
}
