package com.upuphone.cloudplatform.usercenter.auth.vo.checkloginstate;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Description: 检查登录态
 *
 * @author hanzhumeng
 * Created: 2022/4/26
 */
@ApiModel("检查登录态request")
@Data
public class CheckLoginStateRequest {
    // 由于采用网关验证，该request为空
}
