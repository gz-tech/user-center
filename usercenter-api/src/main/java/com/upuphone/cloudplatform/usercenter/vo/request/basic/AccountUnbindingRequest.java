package com.upuphone.cloudplatform.usercenter.vo.request.basic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ApiModel(value = "帐户绑定 request")
public class AccountUnbindingRequest {

    @ApiModelProperty("绑定类型 1 微信")
    @NotNull
    private Integer bingdingType;
}
