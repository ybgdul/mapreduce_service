package mapreduce.app.services;

import java.time.Instant;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Job;
import mapreduce.app.entities.JobEstimate;
import mapreduce.app.entities.RuntimeEstimationModel;
import mapreduce.app.repositories.JobEstimateRepo;
import mapreduce.app.utilities.DTOs.JobContext;

@Service
@RequiredArgsConstructor
public class EstimateService {
    
    private final JobEstimateRepo estimateRepo;
    private final RuntimeEstimationModel model;

    public JobEstimate estimateAndCreateJobEstimate(Job job, JobContext context) { 

        double seconds = model.predict(context);

        JobEstimate estimate = new JobEstimate();
        estimate.setJob(job);
        estimate.setEstimate(seconds);
        estimate.setCreatedAt(Instant.now());
        estimate.setVersion((long) 1);
        estimate.setChunkSize(context.inputSize());
        estimate.setThreadPoolSize(context.threadPoolSize());

        estimateRepo.save(estimate);

        return estimate;
    }

    public JobEstimate estimateAndPersistEstimate(JobEstimate estimate, double average, long remainingTasks, long runningTasks) { 
        JobContext context = new JobContext(estimate.getChunkSize(), remainingTasks, estimate.getThreadPoolSize(), runningTasks, average);
        double seconds = model.predict(context);

        estimate.setEstimate(seconds);

        estimateRepo.save(estimate);
        
        return estimate;
    }
}
