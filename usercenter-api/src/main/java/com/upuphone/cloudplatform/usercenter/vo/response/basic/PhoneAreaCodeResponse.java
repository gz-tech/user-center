package com.upuphone.cloudplatform.usercenter.vo.response.basic;

import com.upuphone.cloudplatform.usercenter.vo.PhoneAreaCode;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel(value = "获取全部电话区号 response")
public class PhoneAreaCodeResponse {
    private List<PhoneAreaCode> phoneAreaCodes;
}
