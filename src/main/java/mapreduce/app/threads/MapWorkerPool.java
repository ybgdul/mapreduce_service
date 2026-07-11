package mapreduce.app.threads;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import mapreduce.app.components.TaskRegistry;
import mapreduce.app.entities.Task;
import mapreduce.app.utilities.Interfaces.TaskService;

@Component
public class MapWorkerPool {
    
    private final Executor executor;
    private final TaskRegistry registry;

    public MapWorkerPool(@Qualifier("mapWorkerPool") Executor executor, TaskRegistry registry) { 
        this.executor = executor;
        this.registry = registry;
    }

    public void submit(Task task) { 
        TaskService service = registry.findService(task.getJobType());
        executor.execute(
            () -> {
                service.executeMapTask(task);
            }
        );
    }
}
