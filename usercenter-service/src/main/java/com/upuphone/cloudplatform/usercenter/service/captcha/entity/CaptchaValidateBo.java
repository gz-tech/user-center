package com.upuphone.cloudplatform.usercenter.service.captcha.entity;

import com.upuphone.cloudplatform.usercenter.constants.CaptchaBusinessEnum;
import lombok.Data;

/**
 * @Classname CaptchaValidateBo
 * @Description
 * @Date 2022/6/1 5:33 下午
 * @Created by gz-d
 */
@Data
public class CaptchaValidateBo {
    private CaptchaBusinessEnum captchaBusinessType;

    private String validate;

    /**
     * 校验者身份id，登录态是userId，非登录态是phoneNumber或email
     */
    private String uniqueId;
}
