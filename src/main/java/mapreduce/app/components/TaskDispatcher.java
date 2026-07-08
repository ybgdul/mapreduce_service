package mapreduce.app.components;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Task;
import mapreduce.app.threads.MapWorkerPool;
import mapreduce.app.threads.ReduceWorkerPool;
import mapreduce.app.utilities.Enums.TaskType;

@RequiredArgsConstructor
@Component
public class TaskDispatcher {
    
    private final MapWorkerPool mapPool;
    private final ReduceWorkerPool reducePool;

    public void dispatch(Task task) { 

        if(task.getTaskType() == TaskType.MAP) { 
            mapPool.submit(task);
        }
        else {
            reducePool.submit(task);
        }
    }

}
