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
import mapreduce.app.utilities.Enums.TaskType;

@Component
@RequiredArgsConstructor
public class TaskGenerator {
    
    private final TaskRepo taskRepo;
    private static final int CHUNK_SIZE = 128;

    public void generate(Job job, StorageFileDto metadata) { 

        Long length = metadata.length();

        List<Task> tasks = new ArrayList<>();

        for(long i = 0; i < length; i += CHUNK_SIZE) { 
            Task task = new Task();
            if(i + CHUNK_SIZE > length) {task.setEndOffset(length); task.setStartOffset(i);}
            else {task.setEndOffset(i + CHUNK_SIZE); task.setStartOffset(i); }
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

}
