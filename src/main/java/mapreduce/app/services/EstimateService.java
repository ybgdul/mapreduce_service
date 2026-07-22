package mapreduce.app.services;

import java.time.Instant;

import org.springframework.stereotype.Service;

import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
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

    public JobEstimate estimateAndCreateJob(Job job, JobContext context) { 

        double seconds = model.predict(context);

        JobEstimate estimate = new JobEstimate();
        estimate.setJob(job);
        estimate.setEstimate(seconds);
        estimate.setCreatedAt(Instant.now());
        estimate.setVersion((long) 1);

        estimateRepo.save(estimate);

        return estimate;
    }

    @Transactional
    public void recordExecutionTime(Job job, double timeTaken) { 
        int tries = 3;

        while(tries-- > 0) { 
            try { 
                
            } catch (OptimisticLockException e) { 
                
            }
        }
    }
}
