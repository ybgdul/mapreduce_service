package mapreduce.app.components;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mapreduce.app.utilities.Enums.JobType;
import mapreduce.app.utilities.Interfaces.TaskService;

@Component
@RequiredArgsConstructor
public class TaskRegistry {
    
    private final Map<JobType, TaskService> map;

    public TaskRegistry(List<TaskService> services) { 
        this.map = services.stream().collect(Collectors.toUnmodifiableMap(TaskService::getJobType, Function.identity()));
    }

}
