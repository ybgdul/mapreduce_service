package mapreduce.app.threads;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import mapreduce.app.entities.Task;



@Component
public class ReduceWorkerPool {
    private final Executor executor;
    private final ReduceTaskExecutor taskExecutor;

    public ReduceWorkerPool(@Qualifier("reduceWorkerPool") Executor executor, ReduceTaskExecutor taskExecutor) { 
        this.executor = executor;
        this.taskExecutor = taskExecutor;
    }

    public void submit(Task task) { 
        executor.execute(
            () -> {
                taskExecutor.execute(task);
            }
        );
    }
}
