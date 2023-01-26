package com.upuphone.cloudplatform.usercenter.aop.handler;

import com.upuphone.cloudplatform.common.context.ContextAttributeType;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.interceptor.checkheader.CheckHeaderStrategy;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/20
 */
@Component
@Slf4j
public class UserIdCheckHeaderStrategy implements CheckHeaderStrategy {

    @Override
    public void check(String param, String name, Class<?> cls) {
        if (!name.equals(ContextAttributeType.USER_ID.getName())) {
            log.error("请求头非X-user-id，name=[{}], param=[{}]", name, param);
            throw new BusinessException(CommonErrorCode.PARAM_ERROR);
        }
        if (StringUtils.isBlank(param)) {
            throw new BusinessException(UserCenterErrorCode.NOT_LOGIN_ERROR);
        }
        if (!StringUtils.isNumeric(param)) {
            log.error("X-user-id非数字, name=[{}], param=[{}]", name, param);
            throw new BusinessException(CommonErrorCode.PARAM_ERROR);
        }
        try {
            Long.valueOf(param);
        } catch (Exception e) {
            log.error("X-user-id非Long, name=[{}], param=[{}]", name, param);
            throw new BusinessException(CommonErrorCode.PARAM_ERROR);
        }
    }
}
