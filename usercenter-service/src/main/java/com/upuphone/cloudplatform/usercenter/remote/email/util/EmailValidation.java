package com.upuphone.cloudplatform.usercenter.remote.email.util;

import com.upuphone.cloudplatform.usercenter.constants.ApiConstants;

import java.util.regex.Pattern;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/3/3
 */
public final class EmailValidation {

    private EmailValidation() {

    }

    public static boolean patternMatches(String emailAddress) {
        return Pattern.compile(ApiConstants.EMAIL_EXP).matcher(emailAddress).matches();
    }
}
