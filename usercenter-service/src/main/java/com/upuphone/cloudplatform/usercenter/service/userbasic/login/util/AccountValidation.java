package com.upuphone.cloudplatform.usercenter.service.userbasic.login.util;

import com.upuphone.cloudplatform.usercenter.constants.ApiConstants;

import java.util.regex.Pattern;

/**
 * @Classname AccountValidation
 * @Description
 * @Date 2022/3/17 5:59 下午
 * @Created by gz-d
 */
public final class AccountValidation {
    public AccountValidation() {
    }

    public static boolean boxingIdMatches(String boxingId) {
        return Pattern.compile(ApiConstants.ACCOUNT_EXP).matcher(boxingId).matches();
    }

    public static boolean phoneMatches(String phone) {
        return Pattern.compile(ApiConstants.MOBILE_EXP).matcher(phone).matches();
    }

    public static boolean emailMatches(String email) {
        return Pattern.compile(ApiConstants.EMAIL_EXP).matcher(email).matches();
    }
}
