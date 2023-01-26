package com.upuphone.cloudplatform.usercenter.vo.response.basic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@ApiModel(value = "帐户绑定列表 response")
public class AccountBindingListResponse {

    private List<Binding> bindings;


    @Setter
    @Getter
    @ApiModel(value = "绑定关系")
    public static class Binding {

        @ApiModelProperty("绑定关系ID")
        private String boundId;

        @ApiModelProperty("绑定名称")
        private String boundName;

        @ApiModelProperty("绑定类型 1 微信")
        private Integer boundType;

        @ApiModelProperty("是否绑定")
        private Boolean isBounded;
    }
}


