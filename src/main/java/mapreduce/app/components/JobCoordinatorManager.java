package mapreduce.app.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.Job;
import mapreduce.app.entities.Task;
import mapreduce.app.repositories.JobRepo;
import mapreduce.app.repositories.MapResultRepo;
import mapreduce.app.repositories.TaskRepo;
import mapreduce.app.services.JobService;
import mapreduce.app.utilities.Enums.JobStatus;
import mapreduce.app.utilities.Enums.TaskType;
import mapreduce.app.utilities.Interfaces.PostProcessService;
import mapreduce.app.utilities.POJOs.JobCoordinator;

@Component
@RequiredArgsConstructor
public class JobCoordinatorManager {

    private final TaskScheduler taskScheduler;
    private final TaskRepo taskRepo;
    private final JobRepo jobRepo;
    private final MapResultRepo mapResultRepo;
    private final TaskGenerator taskGenerator;
    private final JobService jobService;
    private final TaskRegistry taskRegistry;
    
    private final Map<Long, JobCoordinator> coordinators = new ConcurrentHashMap<>();

    public void register(Job job) {
        coordinators.put(job.getId(), new JobCoordinator(this, mapResultRepo, taskGenerator, jobRepo, job.getId()));
    }

    @Scheduled(fixedDelay = 1000)
    public void poll() { 
        //
        coordinators.values().forEach(JobCoordinator::poll);
    }

    @PostConstruct
    private void initialize() {
        List<Task> tasks = taskRepo.getAllCreatedRunningAssignedTasks();
        List<Task> reduces = new ArrayList<>();
        List<Task> maps = new ArrayList<>();

        for(Task task : tasks) { 
            if(task.getTaskType() == TaskType.MAP) maps.add(task);
            else reduces.add(task);
        }

        taskScheduler.pushMapTasks(maps);
        taskScheduler.pushReduceTasks(reduces);

        List<Job> jobs = jobRepo.findAllLeftOutJobs();

        for(Job job : jobs) { 
            register(job);
        }
    }

    public void completeJob(Job job) {
        JobCoordinator coordinator = coordinators.remove(job.getId());
        if(coordinator == null) return;
        job.setStatus(JobStatus.POST_PROCESS);
        jobRepo.save(job);
        PostProcessService service = taskRegistry.findPostProcessService(job.getType());
        service.postProcess(job);
    }

    public void terminateJob(Job job, Exception e) { 
        coordinators.remove(job.getId());
        jobService.totalCleanup(job, e);
    }
}
