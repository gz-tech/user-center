package com.upuphone.cloudplatform.usercenter.auth.vo.getuserbaseinfo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "用户基础信息request")
public class GetUserBaseInfoRequest {

    private String deviceId;

    private Long userId;
}
