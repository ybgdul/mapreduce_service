package mapreduce.app.services;

import java.io.InputStream;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Task;
import mapreduce.app.repositories.MapResultRepo;
import mapreduce.app.repositories.TaskRepo;
import mapreduce.app.utilities.Enums.JobType;
import mapreduce.app.utilities.Interfaces.StorageService;
import mapreduce.app.utilities.Interfaces.TaskService;

@Service
@RequiredArgsConstructor
public class CountWordsTaskService implements TaskService{

    private Task task;

    private final MapResultRepo mapResultRepo;
    private final TaskRepo taskRepo;
    private final StorageService storageService;

    @Override
    public JobType getJobType() {
        return JobType.COUNT_WORDS;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public void executeMapTask(Task task) {
        this.task = task;
        
        InputStream chunk = storageService.loadFile(task.getJob().getId(), task.getStartRange(), task.getEndRange());
        try
        { 
            byte[] bytes = chunk.readAllBytes();
        } catch (IOException e) { 
            //HANDLE EXCEPTION HERER
        }

        long result = count(chunk);
    }

    @Override
    public void executeReduceTask(Task task) {
        this.task = task;

    }

    private Long count(InputStream chunk) { 
        return (long) 10000;
    }
    
}
