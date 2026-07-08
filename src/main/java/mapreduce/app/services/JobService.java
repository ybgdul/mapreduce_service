package mapreduce.app.services;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
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

    private final StorageService storageService;
    private final AppUserRepo userRepo;
    private final JobRepo jobRepo;
    
    public Long submitWordCountJob(MultipartFile file, Long userId) { 
        
        StorageFileDto holder = storageService.store(file);
        AppUser user = userRepo.findById(userId).orElseThrow(() -> new CustomAuthException("User not found by id: " + userId, HttpStatus.NOT_FOUND));

        Job job = new Job();

        job.setCreatedAt(Instant.now());
        job.setInputLocation(holder.location());
        job.setStatus(JobStatus.CREATED);
        job.setType(JobType.COUNT_WORDS);
        job.setUser(user);

        jobRepo.save(job);

    }
}
