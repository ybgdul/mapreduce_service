package mapreduce.app.controllers;

import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Job;
import mapreduce.app.repositories.JobRepo;
import mapreduce.app.services.JobService;
import mapreduce.app.utilities.Enums.JobStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;
    private final JobRepo jobRepo;
    
    @PostMapping("/post/count/{id}")
    public ResponseEntity<?> postCountJob(@RequestBody MultipartFile file, @PathVariable Long id) { 
        Long jobId = jobService.submitWordCountJob(file, id);
        return ResponseEntity.ok("Count job has been submitted: " + jobId);
    }

    @GetMapping("/get/count/{id}")
    public ResponseEntity<?> getCountJob(@PathVariable Long id) {
        Job job = jobRepo.findById(id).orElse(null);
        if(job == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job by id not found: " + id);

        JobStatus status = job.getStatus();

        switch (status) {
            case CREATED, RUNNING -> {
                return ResponseEntity.ok("Job is being processed");
            }
            case FAILED, CANCELLED -> {
                return ResponseEntity.internalServerError().body("Job is failed: " + job.getErrorMessage());
            }
            default -> {
                InputStream inputStream = jobService.getWordCountJob(job);
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"result.txt\"").body(new InputStreamResource(inputStream));
            }
        }
    }
}
