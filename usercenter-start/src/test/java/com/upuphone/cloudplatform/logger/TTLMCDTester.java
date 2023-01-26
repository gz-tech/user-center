package com.upuphone.cloudplatform.logger;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.upuphone.cloudplatform.usercenter.CoreApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreApplication.class)
public class TTLMCDTester {

    @Test
    @Async
    public void testTTLMcd() {
        MDC.put("test", "1111111");
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService = TtlExecutors.getTtlExecutorService(executorService);
        log.info("test1");
        executorService.execute(() -> {

            log.info("test2" + MDC.get("test"));
        });
    }
}
