package com.mexcorgo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Dedicated thread-pool for Excel imports.
 *
 * Why a separate pool?
 *   Without this, {@code @Async} methods use Spring's default
 *   {@code SimpleAsyncTaskExecutor} which creates an unbounded number of
 *   threads.  A dedicated pool gives us:
 *     - Bounded concurrency (max 5 simultaneous imports).
 *     - Queue depth (up to 25 uploads waiting without rejection).
 *     - Named threads (easy to identify in thread dumps / APM).
 *     - Zero impact on Tomcat's HTTP worker pool – regular API requests
 *       are always served regardless of import activity.
 */
@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "leadImportExecutor")
    public Executor leadImportExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);          // always-warm threads
        executor.setMaxPoolSize(5);           // burst capacity
        executor.setQueueCapacity(25);        // uploads buffered before rejection
        executor.setThreadNamePrefix("lead-import-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
