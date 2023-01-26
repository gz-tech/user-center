package com.upuphone.cloudplatform.usercenter.aop;

import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.google.common.base.Strings;
import com.upuphone.cloudplatform.usercenter.aop.annotation.NoCheckInjection;
import com.upuphone.cloudplatform.usercenter.aop.handler.DefaultInjectionAttackHandler;
import com.upuphone.cloudplatform.usercenter.aop.handler.InjectionAttackHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Component
public class InjectionAttackInterceptor extends HandlerInterceptorAdapter {

    private InjectionAttackHandler injectionAttackHandler = DefaultInjectionAttackHandler.getInstance();

    @SuppressWarnings("unchecked")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            log.warn("! ( handler instanceof HandlerMethod )");
            log.warn("handler : {}", handler);
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        NoCheckInjection noCheckInjection = handlerMethod.getMethodAnnotation(NoCheckInjection.class);
        // 需要忽略拦截的字符串数组
        String[] ignoreStr = null;
        if (Objects.nonNull(noCheckInjection)) {
            ignoreStr = noCheckInjection.value();
            // 不需要过滤拦截词
            if (ArrayUtils.isEmpty(ignoreStr)) {
                return true;
            }
        }

        String requestBody = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8).replace("\n", "");

        // 参数注入攻击处理
        if (!Strings.isNullOrEmpty(requestBody) && this.injectionAttackHandler.isInjectionAttack(requestBody, ignoreStr)) {
            log.warn("参数 {} 被判断为注入攻击", requestBody);
            this.injectionAttackHandler.attackHandle(request, response, requestBody);
            return false;
        }

        final Map<String, String> decodedUriVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (decodedUriVariables == null || decodedUriVariables.isEmpty()) {
            return true;
        }

        // URI PATH 注入攻击处理
        for (String decodedUriVariable : decodedUriVariables.values()) {
            if (this.injectionAttackHandler.isInjectionAttack(decodedUriVariable, ignoreStr)) {
                log.warn("URI {} 被判断为注入攻击", requestBody);
                this.injectionAttackHandler.attackHandle(request, response, decodedUriVariable);
                return false;
            }
        }
        return true;
    }
}
