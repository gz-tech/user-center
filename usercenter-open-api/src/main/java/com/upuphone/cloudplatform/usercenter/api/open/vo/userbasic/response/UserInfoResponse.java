package com.upuphone.cloudplatform.usercenter.api.open.vo.userbasic.response;

import com.upuphone.cloudplatform.usercenter.api.vo.UserInfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@ApiModel("用户信息 response")
@Accessors(chain = true)
public class UserInfoResponse {

    @ApiModelProperty(value = "用户信息")
    private List<UserInfoVo> userInfoList;
}
