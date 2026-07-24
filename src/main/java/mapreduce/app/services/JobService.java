package mapreduce.app.services;

import java.io.InputStream;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import mapreduce.app.components.JobCoordinatorManager;
import mapreduce.app.components.TaskGenerator;
import mapreduce.app.entities.AppUser;
import mapreduce.app.entities.Job;
import mapreduce.app.repositories.AppUserRepo;
import mapreduce.app.repositories.JobRepo;
import mapreduce.app.utilities.DTOs.StorageFileDto;
import mapreduce.app.utilities.Enums.JobStatus;
import mapreduce.app.utilities.Enums.JobType;
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

}
