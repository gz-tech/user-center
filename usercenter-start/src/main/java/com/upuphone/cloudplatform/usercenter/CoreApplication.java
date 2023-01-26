package com.upuphone.cloudplatform.usercenter;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRetry
@EnableTransactionManagement
@SpringBootApplication
@EnableFeignClients(basePackages = "com.upuphone.cloudplatform")
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.upuphone")
@MapperScan({"com.upuphone.cloudplatform.usercenter.mybatis.mapper"})
@EnableApolloConfig
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }
}
