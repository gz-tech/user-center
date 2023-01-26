package com.upuphone.cloudplatform.usercenter.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel("国家信息信息VO")
@Getter
@Setter
public class CountryVo {
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("洲id")
    private Integer continentId;

    @ApiModelProperty("国家-英文-标准名称")
    private String name;


    @ApiModelProperty("国家-英文-小写")
    private String lowerName;


    @ApiModelProperty("国家-英文-代码")
    private String countryCode;


    @ApiModelProperty("国家-英文-名称全称")
    private String fullName;


    @ApiModelProperty("国家-中文-常用标准名称")
    private String cname;


    @ApiModelProperty("国家-中文-全称名称")
    private String fullCname;


    @ApiModelProperty("国家-说明")
    private String remark;

    @ApiModelProperty("国家-二位编码")
    private String fbCountryCode;

    @ApiModelProperty("国家-语言二位编码")
    private String lang;
}
