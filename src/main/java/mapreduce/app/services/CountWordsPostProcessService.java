package mapreduce.app.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Job;
import mapreduce.app.entities.ReduceResult;
import mapreduce.app.repositories.JobRepo;
import mapreduce.app.repositories.ReduceResultRepo;
import mapreduce.app.utilities.DTOs.CountTaskResult;
import mapreduce.app.utilities.Enums.JobStatus;
import mapreduce.app.utilities.Enums.JobType;
import mapreduce.app.utilities.Exceptions.StorageConnectionException;
import mapreduce.app.utilities.Interfaces.PostProcessService;
import mapreduce.app.utilities.Interfaces.StorageService;
import mapreduce.app.utilities.POJOs.StorageInputStream;

@Service
@RequiredArgsConstructor
public class CountWordsPostProcessService implements PostProcessService{

    private final JobRepo jobRepo;
    private final ReduceResultRepo reduceResultRepo;
    private final StorageService storageService;
    private final DeletionService deletionService;
    private final ObjectMapper objectMapper;
    
    @Override
    public void postProcess(Job job) {
        List<ReduceResult> results = reduceResultRepo.findAllByJob(job);
        //
        long answer = (long) 0;

        for(ReduceResult result : results) { 
            CountTaskResult countResult = null;
            try (InputStream json = loadResult(result)) { 
                countResult = objectMapper.readValue(json, CountTaskResult.class);
                answer += countResult.count();
            } catch (StorageConnectionException e)  {
                //retry logic
            } catch (IOException e) {
                deletionService.terminateReduceResult(result, job);
            }

        }

        job.setStatus(JobStatus.COMPLETED);
        CountTaskResult jobResult = new CountTaskResult((long) 0, (long) job.getTotalTasks(), answer);
        storageService.storeResult(job.getId(), jobResult);
        jobRepo.save(job);

    }

    @Override
    public JobType getJobType() {
        return JobType.COUNT_WORDS;
    }

    private InputStream loadResult(ReduceResult result) { 
        InputStream raw = storageService.loadReduceResult(result.getJob().getId(), result.getTask().getId());
        return new StorageInputStream(raw);
    }
}
