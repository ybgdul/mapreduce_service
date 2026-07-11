package mapreduce.app.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.MapResult;
import mapreduce.app.entities.Task;
import mapreduce.app.repositories.MapResultRepo;
import mapreduce.app.repositories.TaskRepo;
import mapreduce.app.utilities.DTOs.CountTaskResult;
import mapreduce.app.utilities.Enums.JobType;
import mapreduce.app.utilities.Enums.TaskStatus;
import mapreduce.app.utilities.Exceptions.WordCountException;
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
        task.setStatus(TaskStatus.RUNNING);
        taskRepo.saveAndFlush(task);
        InputStream chunk = storageService.loadFile(task.getJob().getId(), task.getStartRange(), task.getEndRange());
        long count = (long) -1;
        try{ 
            count = count(chunk);
        } catch (IOException e) { 
            task.setStatus(TaskStatus.FAILED);
            taskRepo.save(task);
            throw new WordCountException("Failed to count the words: " + e.getMessage());
        }

        CountTaskResult result = new CountTaskResult(task.getStartRange(), task.getEndRange(), count);

        task.setStatus(TaskStatus.COMPLETED);
        taskRepo.save(task);
        storageService.storeMapResult(task.getJob().getId(),task.getId(), result);
    }

    @Override
    public void executeReduceTask(Task task) {
        this.task = task;
        task.setStatus(TaskStatus.RUNNING);
        taskRepo.saveAndFlush(task);

        List<MapResult> results = mapResultRepo.getAllResultsBySequenceAndJobId(task.getJob().getId(), task.getStartRange(), task.getEndRange());

        

    }
    
    private long count(InputStream input) throws IOException { 
        long count = 0;
        try (BufferedReader reader = new BufferedReader( new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;

            while((line = reader.readLine()) != null) { 
                if(!line.isBlank()) {
                    count += line.trim().split("\\s+").length;
                }
            }
        }

        return count;
    }
}
