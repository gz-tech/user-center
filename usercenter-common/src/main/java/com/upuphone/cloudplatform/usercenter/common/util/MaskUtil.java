package com.upuphone.cloudplatform.usercenter.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/25
 */
public class MaskUtil {

    private static final String MASK_PHONE_NUMBER = "(\\d{3})\\d+(\\d{2})";

    private static final String MASK_EMAIL = "(\\w{3})(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)";

    private static final String MASK_EMAIL_SHORT = "(\\w)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)";

    private static final String MASK_ID_CARD = "(\\d{4})\\d{10}(\\w{4})";

    /**
     * 隐藏手机号码 保留前3后2
     *
     * @param phoneNumber phoneNumber
     * @return 手机号掩码
     */
    public static String maskPhone(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            return null;
        }
        return phoneNumber.replaceAll(MASK_PHONE_NUMBER, "$1******$2");
    }

    /**
     * 隐藏邮箱
     *
     * @param email email
     * @return 邮箱掩码
     */
    public static String maskEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return null;
        }
        String result = email.replaceAll(MASK_EMAIL, "$1****$4");
        if (StringUtils.equalsIgnoreCase(email, result)) {
            return result.replaceAll(MASK_EMAIL_SHORT, "$1**$4");
        }
        return result;
    }

    /**
     * 隐藏身份证 保留前后4位
     *
     * @param idCard idCard
     * @return 身份证掩码
     */
    public static String maskIdCard(String idCard) {
        if (StringUtils.isBlank(idCard)) {
            return null;
        }
        return idCard.replaceAll(MASK_ID_CARD, "$1*****$2");
    }
}
