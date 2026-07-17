package mapreduce.app.services;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Job;
import mapreduce.app.entities.MapResult;
import mapreduce.app.entities.ReduceResult;
import mapreduce.app.entities.Task;
import mapreduce.app.repositories.JobRepo;
import mapreduce.app.repositories.MapResultRepo;
import mapreduce.app.repositories.ReduceResultRepo;
import mapreduce.app.repositories.TaskRepo;
import mapreduce.app.utilities.Enums.JobStatus;
import mapreduce.app.utilities.Enums.TaskStatus;
import mapreduce.app.utilities.Interfaces.StorageService;

@Service
@RequiredArgsConstructor
public class DeletionService {

    private final StorageService storageService;
    private final TaskRepo taskRepo;
    private final MapResultRepo mapResultRepo;
    private final ReduceResultRepo reduceResultRepo;
    private final JobRepo jobRepo;
    
    
    @Transactional
    public void terminate(Job job, Exception e) { 
        job.setStatus(JobStatus.CANCELLED);
        job.setErrorMessage(e.getMessage());
        storageService.terminate(job.getId());
        taskRepo.deleteAllByJob(job);
        mapResultRepo.deleteAllByJob(job);
        reduceResultRepo.deleteAllByJob(job);
    }

    public void terminateReduce(List<MapResult> results, Long currentSubsequence, Task task) {
        MapResult toDelete = null;

        for(int i = 0; i < results.size(); i++) {
            MapResult currentResult = results.get(i); 
            if(Objects.equals(currentResult.getSequence(), currentSubsequence)) {toDelete = currentResult; results.remove(i);}
            currentResult.setClaimed(false);
        }
        Task mapTask = toDelete.getTask();
        mapTask.setStatus(TaskStatus.ASSIGNED);
        
        storageService.delete(toDelete.getJob().getId(), toDelete.getId(), true);

        taskRepo.save(mapTask);
        taskRepo.deleteById(task.getId());
        mapResultRepo.saveAll(results);
        mapResultRepo.deleteById(toDelete.getId());
        return;
    }

    public void terminateReduceResult(ReduceResult reduceResult, Job job) { 
        Task reduce = taskRepo.findById(reduceResult.getTask().getId()).orElse(null);
        if(reduce == null) return;

        List<MapResult> mapResults = mapResultRepo.getAllResultsBySequenceAndJobId(job.getId(), reduce.getStartRange(), reduce.getEndRange());

        for(MapResult result : mapResults) { 
            result.setClaimed(false);
        }
        job.setStatus(JobStatus.RUNNING);

        reduceResultRepo.delete(reduceResult);
        taskRepo.delete(reduce);
        storageService.delete(job.getId(), reduce.getId(), false);

        jobRepo.save(job);
        mapResultRepo.saveAll(mapResults);
    }
}
