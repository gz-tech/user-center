package com.upuphone.cloudplatform.usercenter.vo.response.basic;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "上传照片 response")
public class UpLoadPhotoResponse {
    private String fileId;
    private String url;
}
