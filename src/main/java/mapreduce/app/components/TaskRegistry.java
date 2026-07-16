package mapreduce.app.components;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mapreduce.app.utilities.Enums.JobType;
import mapreduce.app.utilities.Exceptions.UnknownJobException;
import mapreduce.app.utilities.Interfaces.PostProcessService;
import mapreduce.app.utilities.Interfaces.TaskService;

@Component
@RequiredArgsConstructor
public class TaskRegistry {
    
    private final Map<JobType, TaskService> taskMap;
    private final Map<JobType, PostProcessService> postProcessMap;

    public TaskRegistry(List<TaskService> tasks, List<PostProcessService> postProcesses) { 
        this.taskMap = tasks.stream().collect(Collectors.toUnmodifiableMap(TaskService::getJobType, Function.identity()));
        this.postProcessMap = postProcesses.stream().collect(Collectors.toUnmodifiableMap(PostProcessService::getJobType, Function.identity()));
    }

    public TaskService findTaskService(JobType jobType) { 
        TaskService service = taskMap.get(jobType);
        if(service == null) throw new UnknownJobException("Service of job type: " + jobType + " is not found");
        return service;
    }

    public PostProcessService findPostProcessService(JobType jobType) { 
        PostProcessService service = postProcessMap.get(jobType);
        if(service == null) throw new UnknownJobException("Service of job type: " + jobType + " is not found");
        return service;
    }

}
