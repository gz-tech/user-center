package com.upuphone.cloudplatform.usercenter.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@ApiModel(value = "用户信息更新参数")
public class UpdateUserInfoRequest {
    @ApiModelProperty(value = "铂星id")
    private String boxingId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "性别0 未设置 1male 2 female 3保密")
    @Range(min = 0, max = 3, message = "gender must range in 0-3")
    private Integer gender;

    @ApiModelProperty(value = "国家代号")
    private String countryCode;

    @ApiModelProperty(value = "国家名")
    private String countryName;

    @ApiModelProperty(value = "头像存储路径")
    private String photoUrl;
}
