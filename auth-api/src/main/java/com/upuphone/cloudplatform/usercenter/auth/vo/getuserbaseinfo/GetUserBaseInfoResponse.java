package com.upuphone.cloudplatform.usercenter.auth.vo.getuserbaseinfo;

import com.upuphone.cloudplatform.common.model.mobile.UserInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "用户基础信息response")
public class GetUserBaseInfoResponse {

    @ApiModelProperty(value = "userInfo", required = true)
    private UserInfo userInfo;
}
