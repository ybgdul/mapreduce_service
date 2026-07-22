package mapreduce.app.components;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import mapreduce.app.entities.Job;
import mapreduce.app.entities.Task;
import mapreduce.app.repositories.JobRepo;
import mapreduce.app.repositories.TaskRepo;
import mapreduce.app.services.EstimateService;
import mapreduce.app.utilities.DTOs.JobContext;
import mapreduce.app.utilities.DTOs.StorageFileDto;
import mapreduce.app.utilities.Enums.JobStatus;
import mapreduce.app.utilities.Enums.TaskStatus;
import mapreduce.app.utilities.Enums.TaskType;

@Component
public class TaskGenerator {
    
    private final JobRepo jobRepo;
    private final TaskRepo taskRepo;
    private final TaskScheduler taskScheduler;
    private final long CHUNK_SIZE;
    private final int threadSize;
    private final double initialTime;
    private final EstimateService estimateService;

    //hand written constructor because i have to inject with @Value immediately
    public TaskGenerator(mapreduce.app.repositories.JobRepo jobRepo, mapreduce.app.repositories.TaskRepo taskRepo, mapreduce.app.components.TaskScheduler taskScheduler, @Value(value = "${count.words.chunk.size}")
            long CHUNK_SIZE, @Value("${total.thread.count}") int threadSize, @Value(value = "${count.words.average.time}") double initialTime, EstimateService estimateService){
        this.jobRepo = jobRepo;
        this.taskRepo = taskRepo;
        this.taskScheduler = taskScheduler;
        this.CHUNK_SIZE = CHUNK_SIZE;
        this.threadSize = threadSize;
        this.initialTime = initialTime;
        this.estimateService = estimateService;
    }


    public void generateMapTasks(Job job, StorageFileDto metadata) { 

        job.setStatus(JobStatus.RUNNING);

        Long length = metadata.length();

        List<Task> tasks = new ArrayList<>();

        long index = 1;
        long count = 0;
        for(long i = 0; i < length; i += CHUNK_SIZE) { 
            Task task = new Task();
            if(i + CHUNK_SIZE > length) {task.setEndRange(length); task.setStartRange(i);}
            else {task.setEndRange(i + CHUNK_SIZE); task.setStartRange(i); }
            task.setSequence(index++);
            task.setStatus(TaskStatus.CREATED);
            task.setCreatedAt(Instant.now());
            task.setJob(job);
            task.setJobType(job.getType());
            task.setTaskType(TaskType.MAP);
            tasks.add(task);
            count++;
        }

        job.setTotalTasks(count);

        JobContext context = new JobContext(CHUNK_SIZE, count, threadSize, (long) 0, initialTime);
        
        estimateService.estimateAndCreateJob(job, context);
        taskRepo.saveAll(tasks);
        job.setTotalTasks(count);
        jobRepo.save(job);

        taskScheduler.pushMapTasks(tasks);
    }

    public void generateReduceTasks(List<List<Long>> lists, Job job) { 
        List<Task> tasks = new ArrayList<>();
        for(List<Long> list : lists) { 
            Task task = new Task();
            task.setStatus(TaskStatus.CREATED);
            task.setTaskType(TaskType.REDUCE);
            task.setJobType(job.getType());
            task.setJob(job);
            task.setSequence((long) -1);
            task.setStartRange(list.get(0));
            task.setEndRange(list.get(1));
            task.setCreatedAt(Instant.now());
            tasks.add(task);
        }

        taskRepo.saveAll(tasks);

        taskScheduler.pushReduceTasks(tasks);
    }

}
