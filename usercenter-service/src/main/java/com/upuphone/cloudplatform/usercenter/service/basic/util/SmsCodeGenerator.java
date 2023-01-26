package com.upuphone.cloudplatform.usercenter.service.basic.util;

import com.upuphone.cloudplatform.common.exception.ServiceException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;

import java.security.SecureRandom;

public class SmsCodeGenerator {
    public static final char[] DEFAULT_CHARACTERS = "0123456789".toCharArray();

    private SecureRandom secureRandom;
    private char[] chars;
    private int length;

    public SmsCodeGenerator(char[] chars, int length) {
        secureRandom = new SecureRandom();
        this.chars = chars;
        this.length = length;
    }

    public SmsCodeGenerator() {
        this(DEFAULT_CHARACTERS, 6);
    }

    public String generate() {
        try {
            StringBuilder builder = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                builder.append(chars[secureRandom.nextInt(chars.length)]);
            }
            String code = builder.toString();
            return code;
        } catch (Exception e) {
            throw new ServiceException(CommonErrorCode.SERVICE_ERROR, "Fail to generate code", e);
        }
    }

    public void setChars(char[] chars) {
        this.chars = chars;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public char[] getChars() {
        return chars;
    }

    public int getLength() {
        return length;
    }
}
