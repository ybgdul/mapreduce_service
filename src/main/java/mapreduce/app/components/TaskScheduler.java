package mapreduce.app.components;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Task;
import mapreduce.app.repositories.TaskRepo;

@Component
@RequiredArgsConstructor
public class TaskScheduler {
    
    private final TaskRepo taskRepo;
    private final TaskDispatcher dispatcher;

    @Scheduled(fixedDelay = 1000)
    public void poll() { 
        List<Task> tasks = taskRepo.getAllAssignedTasks();

        for(Task task : tasks) { 
            dispatcher.dispatch(task);
        }
    }
}
