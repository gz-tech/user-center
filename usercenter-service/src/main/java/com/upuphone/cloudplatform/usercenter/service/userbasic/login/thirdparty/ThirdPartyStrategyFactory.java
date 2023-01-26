package com.upuphone.cloudplatform.usercenter.service.userbasic.login.thirdparty;


import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ThirdPartyStrategyFactory implements InitializingBean {
    @Autowired
    private ApplicationContext applicationContext;

    private static Map<String, ThirdPartyLoginService> strategyMaps = new HashMap<>();

    public ThirdPartyLoginService getThirdPartyLoginServiceByType(String thirdPartyType) {
        ThirdPartyLoginService thirdPartyLoginService = strategyMaps.get(thirdPartyType);
        if (null == thirdPartyLoginService) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "不支持的三方类型");
        }
        return thirdPartyLoginService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, ThirdPartyLoginService> beansOfFuncType = applicationContext.getBeansOfType(ThirdPartyLoginService.class);
        for (Map.Entry<String, ThirdPartyLoginService> entry : beansOfFuncType.entrySet()) {
            strategyMaps.put(entry.getKey(), entry.getValue());
        }
    }
}
