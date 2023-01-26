package com.upuphone.cloudplatform.usercenter.common.util;

import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.UUID;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/2/23
 */
@Slf4j
public class SessionUtil {

    private static final String SESSION_FORMAT = "%s_%s";

    public static String generateSession(String deviceId) {
        if (StringUtils.isBlank(deviceId)) {
            log.error("generateSession参数错误, deviceId=[{}]", deviceId);
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR);
        }
        return String.format(SESSION_FORMAT, deviceId, UUID.randomUUID().toString().replaceAll("-", ""));
    }

    public static boolean checkSessionDevice(String deviceId, String session) {
        try {
            if (StringUtils.isBlank(session) || !session.contains("_")) {
                return false;
            }
            String[] ss = session.split("_");
            return Objects.equals(ss[0], deviceId);
        } catch (Exception e) {
            log.error("checkSession出现异常, session=[{}], e=", session, e);
            return false;
        }
    }

    public static String getSessionTicket(String session) {
        try {
            if (StringUtils.isBlank(session) || !session.contains("_")) {
                return null;
            }
            String[] ss = session.split("_");
            return ss[1];
        } catch (Exception e) {
            log.error("getSessionTicket出现异常, session=[{}], e=", session, e);
            throw new BusinessException(CommonErrorCode.BUSINESS_ERROR);
        }
    }
}
