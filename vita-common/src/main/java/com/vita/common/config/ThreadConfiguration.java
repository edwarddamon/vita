package com.vita.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 公共线程池配置
 *
 * @author Edward
 * @date 2024-02-28 17:25
 */
public class ThreadConfiguration {


    /**
     * 公共线程池
     *
     * @return executor
     */
    @Bean(value = "vitaCommonTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 获取服务器当前的cpu核心数（若为双核，则为真实cpu总线程数，比如：4核双线程 = 8）
        int coreSize = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(coreSize * 2);
        executor.setQueueCapacity(1000);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        return executor;
    }
}
