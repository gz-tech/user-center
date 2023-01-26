package com.upuphone.cloudplatform.usercenter.auth.vo.checkapprovestate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/26
 */
@ApiModel("检查授权状态response")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class CheckApproveStateResponse {

    @ApiModelProperty("状态 - true存在授权态 false不存在授权态")
    private Boolean state;
}
