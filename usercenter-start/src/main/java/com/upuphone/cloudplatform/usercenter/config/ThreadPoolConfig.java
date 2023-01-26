package com.upuphone.cloudplatform.usercenter.config;

import com.upuphone.cloudplatform.usercenter.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/10
 */

@Configuration
public class ThreadPoolConfig {

    @Autowired
    private Setting setting;

    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(setting.getCorePoolSize());
        executor.setKeepAliveSeconds(setting.getKeepAliveSeconds());
        executor.setMaxPoolSize(setting.getMaxPoolSize());
        executor.setQueueCapacity(setting.getQueueCapacity());
        ThreadPoolExecutor.CallerRunsPolicy callerRunsPolicy = new ThreadPoolExecutor.CallerRunsPolicy();
        //对拒绝task的处理策略
        executor.setRejectedExecutionHandler(callerRunsPolicy);
        executor.initialize();
        return executor;
    }
}
