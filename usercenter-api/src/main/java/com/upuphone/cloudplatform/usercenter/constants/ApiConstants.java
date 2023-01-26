package com.upuphone.cloudplatform.usercenter.constants;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2021/12/23
 */
public class ApiConstants {

    public static final String MOBILE_EXP = "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[235-8]\\d{2}" +
            "|4(?:0\\d|1[0-2]|9\\d))|9[0-35-9]\\d{2}|66\\d{2})\\d{6}$";

    public static final String EMAIL_EXP = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public static final String MOBILE_EXP_MSG = "请输入正确的手机号码";

    public static final String PWD_EXP = "^(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9#@.+_-]+$";

    public static final String PWD_EXP_MSG = "密码必须至少包含一个大写和小写字母，且仅能包含大小写字母，数字及#、@、.、+、-、_符号";

    public static final String PWD_LEN_MSG = "密码长度需在8-20之间";

    public static final String AREA_CODE_CHINA = "86";

    public static final String ACCOUNT_EXP = "^[a-zA-Z0-9#.+_-]+$";

    public static final String VALID_CODE_MSG = "验证码不能含有空格或中文";

    public static final String SESSION_TICKET_MSG = "sessionTicket不能为空";

    public static final String DEVICE_ID_MSG = "deviceId不能为空";

    public static final String SIMPLE_LOGIN = "simple";
}
