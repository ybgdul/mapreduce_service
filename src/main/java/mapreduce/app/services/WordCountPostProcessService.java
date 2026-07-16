package mapreduce.app.services;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Job;
import mapreduce.app.entities.ReduceResult;
import mapreduce.app.repositories.ReduceResultRepo;
import mapreduce.app.repositories.TaskRepo;
import mapreduce.app.utilities.DTOs.StorageFileDto;
import mapreduce.app.utilities.Enums.JobStatus;
import mapreduce.app.utilities.Enums.JobType;
import mapreduce.app.utilities.Interfaces.PostProcessService;
import mapreduce.app.utilities.Interfaces.StorageService;

@Service
@RequiredArgsConstructor
public class WordCountPostProcessService implements PostProcessService{

    private final TaskRepo taskRepo;
    private final ReduceResultRepo reduceResultRepo;
    private final StorageService storageService;
    
    @Override
    public void postProcess(Job job) {
        List<ReduceResult> results = reduceResultRepo.findAllByJob(job);
        
        for(ReduceResult result : results) { 
            InputStream json = storageService.loadReduceResult(job.getId(), result.getId());

        }

        StorageFileDto resultFileDto = storageService.storeResult();
        job.setStatus(JobStatus.COMPLETED);

    }

    @Override
    public JobType getJobType() {
        return JobType.COUNT_WORDS;
    }
}
