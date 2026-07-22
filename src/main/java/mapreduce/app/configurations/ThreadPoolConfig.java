package mapreduce.app.configurations;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    private final long threadCount;

    public ThreadPoolConfig(@Value("${total.thread.count}") long threadCount) { 
        this.threadCount = threadCount;
    }
    
    @Bean("mapExecutor")
    public Executor mapWorkerPool() { 
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize((int) (threadCount * 0.625));
        executor.setMaxPoolSize((int) (threadCount * 0.625));
        executor.setQueueCapacity(100);

        executor.setThreadNamePrefix("map-worker-");

        executor.initialize();

        return executor;
    }

    @Bean("reduceExecutor")
    public Executor reduceWorkerPool() { 
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize((int) (threadCount * 0.375));
        executor.setMaxPoolSize((int) (threadCount * 0.375));
        executor.setQueueCapacity(50);

        executor.setThreadNamePrefix("reduce-worker-");

        executor.initialize();

        return executor;
    }
    
}
