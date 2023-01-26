package com.upuphone.cloudplatform.usercenter.vo.response.usersecurity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 环境检测
 */
@Data
@ApiModel(value = "是否需要环境检测response")
public class EnvironmentDetectionResponse {
    @ApiModelProperty(value = "是否需要；true-需要；false-不需要")
    private boolean needDetection;
}
