package com.upuphone.cloudplatform.usercenter.vo.request.basic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ApiModel(value = "帐户绑定 request")
public class AccountBindingRequest {

    @ApiModelProperty("授权code")
    @NotNull
    private String code;

    @ApiModelProperty("绑定类型 1 微信")
    @NotNull
    @Range(min = 1, max = 1, message = "类型不存在")
    private Integer bingdingType;
}
