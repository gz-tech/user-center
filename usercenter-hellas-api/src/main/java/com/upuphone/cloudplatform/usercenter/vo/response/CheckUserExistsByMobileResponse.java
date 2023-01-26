package com.upuphone.cloudplatform.usercenter.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/11
 */
@ApiModel("根据手机号判断用户是否存在 response")
@Data
public class CheckUserExistsByMobileResponse {

    @ApiModelProperty("是否存在")
    private Boolean result;
    @ApiModelProperty("星纪用户ID")
    private String userId;
}
