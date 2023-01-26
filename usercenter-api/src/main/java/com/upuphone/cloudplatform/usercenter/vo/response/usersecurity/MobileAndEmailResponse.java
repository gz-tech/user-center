package com.upuphone.cloudplatform.usercenter.vo.response.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/27
 */
@Data
@ApiModel("获取该用户的手机号及邮箱")
public class MobileAndEmailResponse {

    @ApiModelProperty("userId")
    private String userId;
    @ApiModelProperty("绑定手机号掩码")
    private String mobile;
    @ApiModelProperty("安全手机号掩码 可能为空")
    private String securityMobile;
    @ApiModelProperty("绑定手机号区号")
    private String mobileCode;
    @ApiModelProperty("安全手机号区号 可能为空")
    private String securityMobileCode;
    @ApiModelProperty("用户邮箱掩码 可能为空")
    private String email;
    @ApiModelProperty("用户星纪号")
    private String boxingId;
}
