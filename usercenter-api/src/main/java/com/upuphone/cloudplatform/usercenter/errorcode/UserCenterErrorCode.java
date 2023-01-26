package com.upuphone.cloudplatform.usercenter.errorcode;

import com.upuphone.cloudplatform.common.response.ErrorCode;

/**
 * @author guangzheng.ding
 * @date 2021/12/17 15:27
 */
public enum UserCenterErrorCode implements ErrorCode {
    //
    AUTHENTICATION_ERROR(300001, "认证失败"),
    LOGIN_TYPE_ERROR(300002, "login type error"),
    NOT_REGISTERED_ERROR(300003, "用户未注册"),
    NOT_LOGIN_ERROR(300004, "登录失效，请重新登录"),
    INVALID_TOKEN(300005, "无效 token"),
    ALREADY_LOGIN(300006, "用户已登录"),
    APP_NOT_REGISTER(300007, "系统未注册"),
    ALREADY_REGISTERED(300008, "该帐号已注册"),
    ACCOUNT_BLOCKED(300009, "密码错误已达次数，请明日尝试"),
    VALIDCODE_TIMES_LIMIT(300010, "获取验证码次数已达本日上限"),
    USER_NOT_FOUND(300011, "帐号不存在"),
    PASSWORD_ERROR(300012, "帐号名或登录密码不正确"),
    DOUBLE_CHECK(300013, "need double check"),
    VALIDCODE_ERROR(300014, "验证码输入不正确"),
    TICKET_ERROR(300015, "二次凭证校验失败"),
    STEP_SESSION_EXPIRED(300016, "停留时间过长，页面已失效"),
    STEP_SESSION_CONFLICTED(300017, "有不同设备正在修改该信息，请稍后再试"),
    VALIDCODE_SEND_GAP_TOO_SHORT(300018, "距离上次发送验证码间隔过短，请稍后再试"),
    VALIDCODE_SEND_FAILED(300019, "短信发送失败"),
    VALIDCODE_SEND_PARAM_ERROR(300020, "短信发送参数错误"),
    EMAIL_NOT_EXISTS(300021, "邮箱地址未设置"),
    REFRESH_TOKEN_EXPIRED(300022, "refresh token 过期，请重新登录"),
    PASSWORD_ERROR_HINT(300023, "你可能需要找回密码"),
    VALIDCODE_CHECK_NOT_SUPPORTED(300024, "不支持该验证类型"),
    DUPLICATE_BOXING_ID(300025, "星纪号已存在，设置失败"),
    LOGIN_TYPE_NOT_SUPPORT(300026, "不支持的登录类型"),
    BINDING_FAIL(300027, "绑定失败"),
    BIND_MOBILE_DUPLICATED(300028, "该手机号已被绑定"),
    BIND_EMAIL_DUPLICATED(300029, "该邮箱已被绑定"),
    APP_NOT_SUPPORT_LOGIN(300030, "APP不支持此登录方式"),
    THIRD_PART_ALREADY_BINDED(300031, "该帐号已绑定其他星纪号，请先解绑"),
    XIJING_ALREADY_BINDED(300032, "星纪号已绑定，请先解绑"),
    CAPTCHA_VALIDATE_ERROR(300033, "captcha校验失败"),
    TEXT_VALIDATE_ERROR(300034, "文本校验失败");

    private int errorCode;
    private String errorMessage;


    UserCenterErrorCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
