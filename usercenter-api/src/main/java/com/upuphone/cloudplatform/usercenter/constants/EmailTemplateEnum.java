package com.upuphone.cloudplatform.usercenter.constants;

import java.util.Arrays;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/4
 */
public enum EmailTemplateEnum {

    REGISTER(ValidCodeType.REGISTER, "[星纪时代]注册邮箱", "您正在注册星纪帐号，验证码[%s],%s分钟之内有效，为了您的安全，请勿泄露给他人", false),
    CHANGE_EMAIL_VALID(ValidCodeType.CHANGE_EMAIL_VALID, "[星纪时代]绑定邮箱", "您正在更改登录邮箱，验证码[%s],%s分钟之内有效，为了您的安全，请勿泄露给他人", false),
    CHANGE_EMAIL(ValidCodeType.CHANGE_EMAIL, "[星纪时代]绑定邮箱", "您正在更改登录邮箱，验证码[%s],%s分钟之内有效，为了您的安全，请勿泄露给他人", false),
    LOGIN(ValidCodeType.LOGIN, "[星纪时代]登录帐号", "您正在登录帐号，验证码[%s],[%s]分钟之内有效，为了您的安全，请勿泄露给他人", false),
    FORGOT_PASSWORD_VALID(ValidCodeType.FORGOT_PASSWORD_VALID, "[星纪时代]找回密码", "您正在找回密码，验证码[%s],%s分钟之内有效，为了您的安全，请勿泄露给他人", false),
    CHANGE_SECURITY_PHONE_VALID(ValidCodeType.CHANGE_SECURITY_MOBILE_VALID, "[星纪时代]修改安全手机号", "您正在修改安全手机号，验证码[%s],%s分钟之内有效，为了您的安全，请勿泄露给他人", false),
    CHANGE_BIND_MOBILE_VALID(ValidCodeType.CHANGE_BIND_MOBILE_VALID, "[星纪时代]修改绑定手机号", "您正在绑定或者修改手机号，验证码[%s],%s分钟之内有效，为了您的安全，请勿泄露给他人", false),
    NEW_DEVICE_LOGIN(ValidCodeType.NEW_DEVICE_EMAIL, "[星纪时代]登录帐号", "您的帐号正在新设备上登录，验证码[%s],%s分钟之内有效，为了您的安全，请勿泄露给他人", false),
    DEVICE_REMOVED(null, "[星纪时代]设备登出", "您的帐号[%s]，于[%s]在[%s]设备上强制登出，如非本人操作，请尽快修改密码", true),
    SDK_VALIDATE(ValidCodeType.SDK_VALIDATE, "[星纪时代]本人操作验证", "请确认是否本人操作，验证码[%s],[%s]分钟之内有效，为了您的安全，请勿泄露给他人", false),
    ;
    private final String subject;

    private final String text;

    private final ValidCodeType codeType;

    /**
     * 是否需要帐号
     */
    private final boolean needAccount;

    EmailTemplateEnum(ValidCodeType type, String subject, String text, boolean needAccount) {
        this.subject = subject;
        this.text = text;
        this.codeType = type;
        this.needAccount = needAccount;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public ValidCodeType getCodeType() {
        return codeType;
    }

    public boolean isNeedAccount() {
        return needAccount;
    }

    public static EmailTemplateEnum getByValidCodeType(ValidCodeType type) {
        return Arrays.stream(values()).filter(o -> o.getCodeType() == type).findFirst().orElse(null);
    }
}
