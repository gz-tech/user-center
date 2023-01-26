package com.upuphone.cloudplatform.usercenter.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public enum ValidCodeType {
    REGISTER(1, "register"),
    LOGIN(2, "login"),
    RESET_PASSWORD(3, "resetPassword"),
    /**
     * 修改绑定手机验证新绑定手机
     */
    CHANGE_BIND_MOBILE(4, "changeBindMobile"),
    /**
     * 修改绑定手机验证当前安全手机/绑定手机/邮箱
     */
    CHANGE_BIND_MOBILE_VALID(5, "changeBindMobileValid"),
    /**
     * 修改安全手机验证当前安全手机/绑定手机/邮箱
     */
    CHANGE_SECURITY_MOBILE_VALID(6, "changeSecurityMobileValid"),
    /**
     * 修改安全手机验证新安全手机
     */
    CHANGE_SECURITY_MOBILE(7, "changeSecurityMobile"),
    /**
     * 忘记密码验证当前安全手机/绑定手机/邮箱
     */
    FORGOT_PASSWORD_VALID(8, "forgotPasswordValid"),
    /**
     * 安全手机号登录
     */
    SECURITY_PHONE_LOGIN(9, "securityPhoneLogin"),
    /**
     * 新设备登录
     */
    NEW_DEVICE_PHONE_LOGIN(10, "newDevicePhoneLogin"),
    /**
     * 更换邮箱验证当前安全手机/绑定手机/邮箱
     */
    CHANGE_EMAIL_VALID(11, "changeEmailValid"),
    /**
     * 更换邮箱验证新邮箱
     */
    CHANGE_EMAIL(12, "changeEmail"),
    /**
     * 新设备邮箱验证
     */
    NEW_DEVICE_EMAIL(13, "newDeviceEmailLogin"),
    /**
     * 客户端sdk验证码认证
     */
    SDK_VALIDATE(14, "sdkValid"),
    REGISTER_LOGIN_COMBINE(15, "registerLoginCombine");

    private final Integer type;
    private final String name;

    ValidCodeType(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }


    /**
     * 可以无需输入手机号获取验证码的情况
     */
    private static final Set<ValidCodeType> TYPES_FOR_WITHOUT_PHONE_NUMBER;
    /**
     * 可以输入手机号获取验证码的情况
     */
    private static final Set<ValidCodeType> TYPES_FOR_PHONE_NUMBER;
    /**
     * 可以无需邮箱地址获取验证码的情况
     */
    private static final Set<ValidCodeType> TYPES_FOR_WITHOUT_EMAIL_ADDRESS;
    /**
     * 可以输入邮箱地址获取验证码的情况
     */
    private static final Set<ValidCodeType> TYPES_FOR_EMAIL;

    static {
        TYPES_FOR_WITHOUT_PHONE_NUMBER = new HashSet<>();
        TYPES_FOR_WITHOUT_PHONE_NUMBER.addAll(Arrays.asList(CHANGE_BIND_MOBILE_VALID,
                CHANGE_SECURITY_MOBILE_VALID, FORGOT_PASSWORD_VALID, SECURITY_PHONE_LOGIN, NEW_DEVICE_PHONE_LOGIN,
                CHANGE_EMAIL_VALID, SDK_VALIDATE));
        TYPES_FOR_PHONE_NUMBER = new HashSet<>();
        TYPES_FOR_PHONE_NUMBER.addAll(Arrays.asList(REGISTER, LOGIN, CHANGE_BIND_MOBILE, CHANGE_SECURITY_MOBILE, REGISTER_LOGIN_COMBINE));
        TYPES_FOR_WITHOUT_EMAIL_ADDRESS = new HashSet<>();
        TYPES_FOR_WITHOUT_EMAIL_ADDRESS.addAll(Arrays.asList(CHANGE_BIND_MOBILE_VALID, CHANGE_SECURITY_MOBILE_VALID,
                FORGOT_PASSWORD_VALID, CHANGE_EMAIL_VALID, NEW_DEVICE_EMAIL, SDK_VALIDATE));
        TYPES_FOR_EMAIL = new HashSet<>();
        TYPES_FOR_EMAIL.addAll(Arrays.asList(REGISTER, CHANGE_EMAIL));
    }

    public static ValidCodeType getByType(Integer type) {
        for (ValidCodeType validCodeType : ValidCodeType.values()) {
            if (Objects.equals(type, validCodeType.getType())) {
                return validCodeType;
            }
        }
        return null;
    }

    /**
     * 判断是否为不合法的无需手机号发送验证码类型
     *
     * @param type ValidCodeType
     * @return true-不合法类型
     */
    public static boolean isInvalidTypeForWithoutPhoneNumber(ValidCodeType type) {
        return !TYPES_FOR_WITHOUT_PHONE_NUMBER.contains(type);
    }

    /**
     * 判断是否为不合法的手机号发送验证码类型
     *
     * @param type ValidCodeType
     * @return true-不合法类型
     */
    public static boolean isInvalidTypeForPhoneNumber(ValidCodeType type) {
        return !TYPES_FOR_PHONE_NUMBER.contains(type);
    }

    /**
     * 判断是否为不合法的无需邮箱地址发送邮件验证码类型
     *
     * @param type ValidCodeType
     * @return true-不合法类型
     */
    public static boolean isInvalidTypeForWithoutEmailAddress(ValidCodeType type) {
        return !TYPES_FOR_WITHOUT_EMAIL_ADDRESS.contains(type);
    }

    /**
     * 判断是否为不合法的发送邮件验证码类型
     *
     * @param type ValidCodeType
     * @return true-不合法类型
     */
    public static boolean isInvalidTypeForEmailAddress(ValidCodeType type) {
        return !TYPES_FOR_EMAIL.contains(type);
    }
}
