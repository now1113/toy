package site.kimnow.toy.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 사용 가능한 프로세서 수 확인
        int processors = Runtime.getRuntime().availableProcessors();

        // 기본 쓰레드 수
        executor.setCorePoolSize(processors);
        // 최대 쓰레드 수
        executor.setMaxPoolSize(processors);
        // 대기 큐 크기
        executor.setQueueCapacity(50);
        // 쓰레드 이름 접두사
        executor.setThreadNamePrefix("Async-Thread-");
        executor.initialize();
        return executor;
    }
}
