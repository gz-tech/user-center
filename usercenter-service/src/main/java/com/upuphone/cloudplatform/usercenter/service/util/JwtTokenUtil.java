package com.upuphone.cloudplatform.usercenter.service.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

/**
 * @author guangzheng.ding
 * @date 2021/12/14 18:12
 */
@Slf4j
public class JwtTokenUtil {

    @SuppressWarnings("all")
    public static LocalDateTime getLocalDateTime(Object map) {
        if (!(map instanceof LinkedHashMap)) {
            return null;
        }
        LinkedHashMap<Object, Object> value = (LinkedHashMap<Object, Object>) map;
        if (value.isEmpty()) {
            return null;
        }
        return LocalDateTime.of(
                (int) value.get("year"),
                (int) value.get("monthValue"),
                (int) value.get("dayOfMonth"),
                (int) value.get("hour"),
                (int) value.get("minute"),
                (int) value.get("second"),
                (int) value.get("nano"));
    }
}

