package com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@ApiModel("用户信息 request")
public class UserInfoRequest {

    @ApiModelProperty(value = "用户id", required = true)
    @NotEmpty
    private List<String> userIds;
}
