package com.upuphone.cloudplatform.usercenter.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/11
 */
@ApiModel("验证星纪token response")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidateResponse {

    @ApiModelProperty("星纪用户ID")
    private String userId;
}
