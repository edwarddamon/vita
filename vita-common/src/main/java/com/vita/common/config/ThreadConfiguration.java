package com.vita.common.config;

import com.vita.common.domain.common.properties.VitaCommonTaskExecutorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 公共线程池配置
 *
 * @author Edward
 * @date 2024-02-28 17:25
 */
@Import(value = VitaCommonTaskExecutorProperties.class)
public class ThreadConfiguration {

    public static final Integer DEFAULT_QUEUE_CAPACITY = 1000;

    @Autowired
    private VitaCommonTaskExecutorProperties taskExecutorProperties;

    /**
     * 公共线程池
     * <p/>
     * 默认配置：<br/>
     * 线程池核心线程数：CPU核数 <br/>
     * 最大线程数：CPU核数 * 2 <br/>
     * 队列容量：1000 <br/>
     * 拒绝策略：CallerRunsPolicy
     *
     * @return executor
     */
    @Bean(value = "vitaCommonTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 获取服务器当前的cpu核心数（若为双核，则为真实cpu总线程数，比如：4核双线程 = 8）
        int coreSize = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(Objects.nonNull(taskExecutorProperties.getCorePoolSize()) ? taskExecutorProperties.getCorePoolSize() : coreSize);
        executor.setMaxPoolSize(Objects.nonNull(taskExecutorProperties.getMaxPoolSize()) ? taskExecutorProperties.getMaxPoolSize() : coreSize * 2);
        executor.setQueueCapacity(Objects.nonNull(taskExecutorProperties.getQueueCapacity()) ? taskExecutorProperties.getQueueCapacity() : DEFAULT_QUEUE_CAPACITY);
        if (Objects.nonNull(taskExecutorProperties.getKeepAliveSeconds())) {
            executor.setKeepAliveSeconds(taskExecutorProperties.getKeepAliveSeconds());
        }
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        return executor;
    }
}
