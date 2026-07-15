package mapreduce.app.components;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Task;
import mapreduce.app.threads.MapWorkerPool;
import mapreduce.app.threads.ReduceWorkerPool;

@RequiredArgsConstructor
@Component
public class TaskDispatcher {
    
    private final MapWorkerPool mapPool;
    private final ReduceWorkerPool reducePool;

    public void dispatchMap(Task task) { 
        mapPool.submit(task);
    }

    public void dispatchReduce(Task task) { 
        reducePool.submit(task);
    }

}
