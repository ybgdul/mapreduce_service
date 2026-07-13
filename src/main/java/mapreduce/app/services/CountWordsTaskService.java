package mapreduce.app.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.MapResult;
import mapreduce.app.entities.ReduceResult;
import mapreduce.app.entities.Task;
import mapreduce.app.repositories.MapResultRepo;
import mapreduce.app.repositories.ReduceResultRepo;
import mapreduce.app.repositories.TaskRepo;
import mapreduce.app.utilities.DTOs.CountTaskResult;
import mapreduce.app.utilities.DTOs.StorageFileDto;
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
    private final ReduceResultRepo reduceResultRepo;
    private final TaskRepo taskRepo;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;

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

        task.setStatus(TaskStatus.COMPLETED);
        CountTaskResult result = new CountTaskResult( task.getStartRange(), task.getEndRange(), count);
        StorageFileDto storage = storageService.storeMapResult(task.getJob().getId(),task.getSequence(), task.getSequence(), result);
        MapResult mapResult = new MapResult();
        mapResult.setClaimed(false);
        mapResult.setCreatedAt(Instant.now());
        mapResult.setJob(task.getJob());
        mapResult.setSequence(task.getSequence());
        mapResult.setStoragePath(storage.location());
        mapResult.setTask(task);
        mapResultRepo.save(mapResult);
        taskRepo.save(task);
    }

    @Override
    public void executeReduceTask(Task task) {
        this.task = task;
        task.setStatus(TaskStatus.RUNNING);
        taskRepo.saveAndFlush(task);

        List<MapResult> results = mapResultRepo.getAllResultsBySequenceAndJobId(task.getJob().getId(), task.getStartRange(), task.getEndRange());

        long answer = (long) 0;
        for(MapResult result : results) {
            InputStream input = storageService.loadMapResult(result.getJob().getId(), result.getTask().getId(), result.getSequence());
            CountTaskResult countResult = null;
            try {
                countResult = objectMapper.readValue(input, CountTaskResult.class);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            answer += countResult.count();
        }

        task.setStatus(TaskStatus.COMPLETED);
        CountTaskResult taskResult = new CountTaskResult(results.getFirst().getSequence(), results.getLast().getSequence(), answer);
        StorageFileDto storage = storageService.storeReduceResult(task.getJob().getId(), task.getId(), taskResult);
        ReduceResult result = new ReduceResult();
        result.setCreatedAt(Instant.now());
        result.setJob(task.getJob());
        result.setStoragePath(storage.location());
        result.setTask(task);
        reduceResultRepo.save(result);
        taskRepo.save(task);
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
