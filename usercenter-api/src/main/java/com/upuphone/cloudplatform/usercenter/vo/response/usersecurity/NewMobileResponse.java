package com.upuphone.cloudplatform.usercenter.vo.response.usersecurity;

import lombok.Builder;
import lombok.Data;

/**
 * Description: 换绑手机号
 *
 * @author hanzhumeng
 * Created: 2021/12/22
 */
@Data
@Builder
public class NewMobileResponse {

    private String newMobile;
}
