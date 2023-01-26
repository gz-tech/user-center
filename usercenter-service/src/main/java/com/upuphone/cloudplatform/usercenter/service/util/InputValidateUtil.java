package com.upuphone.cloudplatform.usercenter.service.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class InputValidateUtil {

    public static final String SPACE = " ";

    public static final String CHINESE = "[\u4e00-\u9fa5]";

    public static final String NUMBER = "^\\d{4,}$";

    private static final Pattern CHINESE_PATTERN = Pattern.compile(CHINESE);

    private static final Pattern SPACE_PATTERN = Pattern.compile(SPACE);

    public static boolean isContainChinese(String s) {
        return CHINESE_PATTERN.matcher(s).find();
    }

    public static boolean isContainSpace(String s) {
        return SPACE_PATTERN.matcher(s).find();
    }

    public static boolean hasNonNumber(String s) {
        return !StringUtils.isNumeric(s);
    }
}
