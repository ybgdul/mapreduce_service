package mapreduce.app.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Job;
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
import mapreduce.app.utilities.Exceptions.StorageConnectionException;
import mapreduce.app.utilities.Interfaces.StorageService;
import mapreduce.app.utilities.Interfaces.TaskService;
import mapreduce.app.utilities.POJOs.StorageInputStream;

@Service
@RequiredArgsConstructor
public class CountWordsTaskService implements TaskService{

    private Task task;

    private final MapResultRepo mapResultRepo;
    private final ReduceResultRepo reduceResultRepo;
    private final TaskRepo taskRepo;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;
    private final DeletionService deletionService;


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
        Job job = task.getJob();
        taskRepo.saveAndFlush(task);
        InputStream chunk = loadFile(task);
        long count = (long) 0;
        try{ 
            count = count(chunk);
        } catch (StorageConnectionException e) { 
            task.setStatus(TaskStatus.ASSIGNED);
            taskRepo.save(task);
            return;
        } catch (IOException e) { 
            task.setStatus(TaskStatus.FAILED);
            taskRepo.save(task);
            deletionService.terminate(job, e);
        } 

        task.setStatus(TaskStatus.COMPLETED);
        CountTaskResult result = new CountTaskResult( task.getStartRange(), task.getEndRange(), count);
        StorageFileDto storage = storageService.storeMapResult(job.getId(),task.getSequence(), task.getSequence(), result);
        MapResult mapResult = new MapResult();
        mapResult.setClaimed(false);
        mapResult.setCreatedAt(Instant.now());
        mapResult.setJob(job);
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
        long currentSubsequence = results.getFirst().getSequence() - 1;
        for(MapResult result : results) {
            currentSubsequence++;
            CountTaskResult countResult = null;
            try (InputStream input = loadResult(result); ) {             
                countResult = objectMapper.readValue(input, CountTaskResult.class);
                answer += countResult.count();
            } catch (StorageConnectionException e)  {
                task.setStatus(TaskStatus.ASSIGNED);
                taskRepo.save(task);
                return;
            } catch (IOException e) {
                deletionService.terminateReduce(results, currentSubsequence, task);
            }
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
    
    private long count(InputStream chunk) throws IOException, StorageConnectionException { 
        long count = 0;
        String line;

        try (BufferedReader reader = new BufferedReader( new InputStreamReader(chunk, StandardCharsets.UTF_8))) {
            while((line = reader.readLine()) != null) { 
            if(!line.isBlank()) {
                count += line.trim().split("\\s+").length;
            }
            }
        }

        return count;
    }

    private InputStream loadFile(Task task) { 
        InputStream raw = storageService.loadFile(task.getJob().getId(), task.getStartRange(), task.getEndRange());
        return new StorageInputStream(raw);
    }
    
    private InputStream loadResult(MapResult result) { 
        InputStream raw = storageService.loadMapResult(result.getJob().getId(), result.getTask().getId());
        return new StorageInputStream(raw);
    }
}
