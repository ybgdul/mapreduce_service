package mapreduce.app.components;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Job;
import mapreduce.app.repositories.JobRepo;
import mapreduce.app.repositories.MapResultRepo;
import mapreduce.app.utilities.POJOs.JobCoordinator;

@Component
@RequiredArgsConstructor
public class JobCoordinatorManager {

    private final JobRepo jobRepo;
    private final MapResultRepo mapResultRepo;
    private final TaskGenerator taskGenerator;
    
    private final Map<Long, JobCoordinator> coordinators = new ConcurrentHashMap<>();

    public void register(Job job) {
        coordinators.put(job.getId(), new JobCoordinator(mapResultRepo, taskGenerator, jobRepo, job.getId()));
    }

    @Scheduled(fixedDelay = 1000)
    public void poll() { 
        coordinators.values().forEach(JobCoordinator::poll);
    }

    @PostConstruct
    private void initialize() {
        List<Job> jobs = jobRepo.findAllLeftOutJobs();

        for(Job job : jobs) { 
            register(job);
        }
    }
}
