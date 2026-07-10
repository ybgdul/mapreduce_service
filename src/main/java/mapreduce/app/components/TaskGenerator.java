package mapreduce.app.components;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Job;
import mapreduce.app.entities.Task;
import mapreduce.app.repositories.TaskRepo;
import mapreduce.app.utilities.DTOs.StorageFileDto;
import mapreduce.app.utilities.Enums.JobType;
import mapreduce.app.utilities.Enums.TaskStatus;
import mapreduce.app.utilities.Enums.TaskType;

@Component
@RequiredArgsConstructor
public class TaskGenerator {
    
    private final TaskRepo taskRepo;
    private static final int CHUNK_SIZE = 128;

    public void generateMapTasks(Job job, StorageFileDto metadata) { 

        Long length = metadata.length();

        List<Task> tasks = new ArrayList<>();

        long index = 1;
        for(long i = 0; i < length; i += CHUNK_SIZE) { 
            Task task = new Task();
            if(i + CHUNK_SIZE > length) {task.setEndRange(length); task.setStartRange(i);}
            else {task.setEndRange(i + CHUNK_SIZE); task.setStartRange(i); }
            task.setSequence(index++);
            task.setCreatedAt(Instant.now());
            task.setJob(job);
            task.setJobType(job.getType());
            task.setInputReference(job.getInputLocation());
            task.setOutputReference(job.getOutputLocation());
            task.setTaskType(TaskType.MAP);
            tasks.add(task);
        }

        taskRepo.saveAll(tasks);

    }

    public void generateReduceTasks(List<List<Long>> lists, JobType jobType) { 
        List<Task> tasks = new ArrayList<>();
        for(List<Long> list : lists) { 
            Task task = new Task();
            task.setStatus(TaskStatus.CREATED);
            task.setTaskType(TaskType.REDUCE);
            task.setJobType(jobType);
            task.setSequence((long) -1);
            task.setStartRange(list.get(0));
            task.setEndRange(list.get(1));
            task.setCreatedAt(Instant.now());
            tasks.add(task);
        }

        taskRepo.saveAll(tasks);
    }

}
