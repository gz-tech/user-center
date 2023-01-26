package com.upuphone.cloudplatform.usercenter.config;

import com.upuphone.cloudplatform.usercenter.aop.InjectionAttackInterceptor;
import com.upuphone.cloudplatform.usercenter.constants.ValidCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class MVCConfig implements WebMvcConfigurer {

    @Autowired
    private InjectionAttackInterceptor injectionAttackInterceptor;


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(
                new Converter<Integer, ValidCodeType>() {
                    @Override
                    public ValidCodeType convert(Integer type) {
                        return ValidCodeType.getByType(type);
                    }
                }
        );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        //adding custom interceptor
        //        interceptorRegistry.addInterceptor(new LogInterceptor()).addPathPatterns("/**");

        // TODO 会拦截闪验token
        //interceptorRegistry.addInterceptor(injectionAttackInterceptor).addPathPatterns("/**");

    }

    @Bean
    public HttpMessageConverter<String> responseBodyStringConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        return converter;
    }

    /**
     * 修改StringHttpMessageConverter默认配置
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(responseBodyStringConverter());
    }

    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        //resolveLazily属性启用是为了推迟文件解析，以在在UploadAction中捕获文件大小异常
        resolver.setResolveLazily(true);
        //超过10M,写临时文件
        resolver.setMaxInMemorySize(10240);
        resolver.setMaxUploadSize(5120L * 1024 * 1024);
        // 上传文件大小 5M 5*1024*1024
        //standardServletMultipartResolver.(1000 * 1024 * 1024);
        return resolver;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }
}
