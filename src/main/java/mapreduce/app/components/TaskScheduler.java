package mapreduce.app.components;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Task;

@Component
@RequiredArgsConstructor
public class TaskScheduler {
    
    private final TaskDispatcher dispatcher;

    public void pushMapTasks(List<Task> tasks) { 
        for(Task task : tasks) { 
            dispatcher.dispatchMap(task);
        }
    }

    public void pushReduceTasks(List<Task> tasks) { 
        for(Task task : tasks) { 
            dispatcher.dispatchReduce(task);
        }
    }
}
