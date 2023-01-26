package com.upuphone.cloudplatform.usercenter.common.util;

import com.google.common.base.Strings;

public class StringUtil {

    public static String replaceSpace(String phoneNumber) {
        if (Strings.isNullOrEmpty(phoneNumber)) {
            return phoneNumber;
        }
        return phoneNumber.replaceAll(" ", "");
    }
}
