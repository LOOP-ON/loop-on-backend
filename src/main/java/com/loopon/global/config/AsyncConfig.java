package com.loopon.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {
        ThreadFactory factory = Thread.ofVirtual()
                .name("Mail-VT-", 0)
                .factory();

        Executor virtualExecutor = Executors.newThreadPerTaskExecutor(factory);

        return new TaskExecutorAdapter(virtualExecutor);
    }
}
