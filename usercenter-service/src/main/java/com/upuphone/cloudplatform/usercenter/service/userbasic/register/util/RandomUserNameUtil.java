package com.upuphone.cloudplatform.usercenter.service.userbasic.register.util;

import java.util.Random;

public class RandomUserNameUtil {

    private static final String PRE_FIX = "星纪用户";

    private static final char[] CHARACTER = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9'};

    private static final int ENDFIX_LENGTH = 8;

    //指定种子数字
    private static final Random RANDOM = new Random(1000);

    public static String getNickname() {

        StringBuilder endfix = new StringBuilder();
        for (int x = 0; x < ENDFIX_LENGTH; ++x) {
            endfix.append(CHARACTER[RANDOM.nextInt(CHARACTER.length)]);
        }
        return PRE_FIX + endfix;
    }
}
