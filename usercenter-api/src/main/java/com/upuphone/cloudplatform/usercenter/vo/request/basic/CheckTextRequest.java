package com.upuphone.cloudplatform.usercenter.vo.request.basic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.http.client.HttpClient;

import javax.validation.constraints.NotBlank;

/**
 * @Author Min.Jiang
 * @Date 2022/5/30 18:07
 * @Version 1.0
 */

@Data
@ApiModel("文本验证请求")
public class CheckTextRequest {

    @ApiModelProperty(value = "数据唯一标识", required = true)
    @NotBlank(message = "数据唯一标识 不能为空")
    private String dataId;

    @ApiModelProperty(value = "文本信息", required = true)
    @NotBlank(message = "文本信息 不能为空")
    private String content;

}
