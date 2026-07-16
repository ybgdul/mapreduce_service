package mapreduce.app.services;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mapreduce.app.components.JobCoordinatorManager;
import mapreduce.app.components.TaskGenerator;
import mapreduce.app.entities.AppUser;
import mapreduce.app.entities.Job;
import mapreduce.app.entities.MapResult;
import mapreduce.app.entities.Task;
import mapreduce.app.repositories.AppUserRepo;
import mapreduce.app.repositories.JobRepo;
import mapreduce.app.repositories.MapResultRepo;
import mapreduce.app.repositories.ReduceResultRepo;
import mapreduce.app.repositories.TaskRepo;
import mapreduce.app.utilities.DTOs.StorageFileDto;
import mapreduce.app.utilities.Enums.JobStatus;
import mapreduce.app.utilities.Enums.JobType;
import mapreduce.app.utilities.Enums.TaskStatus;
import mapreduce.app.utilities.Exceptions.CustomAuthException;
import mapreduce.app.utilities.Interfaces.StorageService;

@Service
@RequiredArgsConstructor
public class JobService {

    private final TaskGenerator taskGenerator;
    private final StorageService storageService;
    private final AppUserRepo userRepo;
    private final JobRepo jobRepo;
    private final JobCoordinatorManager jobCoordinatorManager;
    private final TaskRepo taskRepo;
    private final MapResultRepo mapResultRepo;
    private final ReduceResultRepo reduceResultRepo;
    
    public Long submitWordCountJob(MultipartFile file, Long userId) { 
        
        AppUser user = userRepo.findById(userId).orElseThrow(() -> new CustomAuthException("User not found by id: " + userId, HttpStatus.NOT_FOUND));

        Job job = new Job();

        StorageFileDto holder = storageService.storeFile(file, job.getId());

        job.setCreatedAt(Instant.now());
        job.setStatus(JobStatus.CREATED);
        job.setType(JobType.COUNT_WORDS);
        job.setUser(user);
        job.setCompletedTasks((long)0);
        job.setFailedTasks((long)0);
        job.setTotalTasks((long)0);

        jobRepo.save(job);

        jobCoordinatorManager.register(job);

        taskGenerator.generateMapTasks(job, holder);

        return job.getId();
    }
    
    public InputStream getWordCountJob(Job job) { 
        return storageService.loadFile(job.getId());
    }

    @Transactional
    public void totalCleanup(Job job, Exception e) { 
        job.setStatus(JobStatus.CANCELLED);
        job.setErrorMessage(e.getMessage());
        storageService.terminate(job.getId());
        taskRepo.deleteAllByJob(job);
        mapResultRepo.deleteAllByJob(job);
        reduceResultRepo.deleteAllByJob(job);
    }

    @Transactional
    public void reduceFailCleanup(Task reduceTask, Long sequence, List<MapResult> mapResults) {
        MapResult toDelete = null;

        for(int i = 0; i < mapResults.size(); i++) {
            MapResult result = mapResults.get(i); 
            if(Objects.equals(result.getSequence(), sequence)) {toDelete = result; mapResults.remove(i);}
            result.setClaimed(false);
        }
        Task mapTask = toDelete.getTask();
        mapTask.setStatus(TaskStatus.ASSIGNED);
        
        storageService.delete(toDelete.getJob().getId(), toDelete.getId(), true);

        taskRepo.save(mapTask);
        taskRepo.deleteById(reduceTask.getId());
        mapResultRepo.saveAll(mapResults);
        mapResultRepo.deleteById(toDelete.getId());
    }

}
