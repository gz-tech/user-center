package com.upuphone.cloudplatform.usercenter.vo.response.basic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Min.Jiang
 * @Date 2022/5/31 12:44
 * @Version 1.0
 */

@Getter
@Setter
@ApiModel(value = "文本检测结果")
public class CheckTextResponse {

    @ApiModelProperty(value = "是否通过", required = true)
    private Boolean isAllowed;

}
