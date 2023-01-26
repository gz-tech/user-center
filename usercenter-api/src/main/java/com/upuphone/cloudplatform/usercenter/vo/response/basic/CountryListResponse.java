package com.upuphone.cloudplatform.usercenter.vo.response.basic;

import com.upuphone.cloudplatform.usercenter.vo.CountryVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel(value = "全部国家 response")
public class CountryListResponse {
    @ApiModelProperty(value = "国家list")
    private List<CountryVo> countries;
}
