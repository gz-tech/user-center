package com.upuphone.cloudplatform.usercenter.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("手机国际区号")
public class PhoneAreaCode {

    @ApiModelProperty(value = "初始字符串")
    private String initial;
    @ApiModelProperty(value = "英文")
    private String en;
    @ApiModelProperty(value = "中文")
    private String cn;
    @ApiModelProperty(value = "电话区号")
    private String code;
}
