package com.upuphone.cloudplatform.usercenter.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/11
 */
@ApiModel("创建lotus帐户 response")
@Data
@NoArgsConstructor
public class CreateAccountIfAbsentResponse {

    @ApiModelProperty("星纪userId")
    private String userId;

    public CreateAccountIfAbsentResponse(String userId) {
        this.userId = userId;
    }
}
