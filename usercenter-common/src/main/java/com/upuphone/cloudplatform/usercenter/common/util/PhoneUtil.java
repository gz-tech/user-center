package com.upuphone.cloudplatform.usercenter.common.util;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import org.apache.commons.lang3.StringUtils;

public class PhoneUtil {

    public static String formatPhoneAreaCode(Integer areaCode) {
        return String.format("%04d", areaCode);
    }

    public static String formatPhoneAreaCode(String areaCode) {
        if (Strings.isNullOrEmpty(areaCode)) {
            return "0086";
        }

        areaCode = areaCode.replaceAll("[\\+]+", "");
        Integer areaCodInt = Ints.tryParse(areaCode);
        if (areaCodInt == null) {
            return "0086";
        }
        return String.format("%04d", areaCodInt);
    }

    public static String formatPhoneNumber(String areaCode, String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            return null;
        }
        return formatPhoneAreaCode(areaCode) + phoneNumber;
    }

    public static String removeFormatAreaCode(String formattedAreaCode) {
        return formattedAreaCode.replaceAll("^(0+)", "");
    }

    public static String getAreaCodeFromFormatted(String formattedPhoneAreaCode) {
        return removeFormatAreaCode(formattedPhoneAreaCode.substring(0, 4));
    }

    public static String getPhoneFromFormatted(String formattedPhoneAreaCode) {
        return formattedPhoneAreaCode.substring(4, 15);
    }
}
