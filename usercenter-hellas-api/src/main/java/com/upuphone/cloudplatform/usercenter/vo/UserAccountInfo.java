package com.upuphone.cloudplatform.usercenter.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/11
 */
@ApiModel("用户信息")
@Data
public class UserAccountInfo {

    @ApiModelProperty("头像URL")
    private String avatar;

    @ApiModelProperty("昵称")
    private String nickName;
}
