package mapreduce.app.utilities.POJOs;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import mapreduce.app.components.JobCoordinatorManager;
import mapreduce.app.components.TaskGenerator;
import mapreduce.app.components.TaskScheduler;
import mapreduce.app.entities.Job;
import mapreduce.app.entities.Task;
import mapreduce.app.repositories.JobEstimateRepo;
import mapreduce.app.repositories.JobRepo;
import mapreduce.app.repositories.MapResultRepo;
import mapreduce.app.services.EstimateService;
import mapreduce.app.utilities.Enums.JobStatus;
import mapreduce.app.utilities.Enums.TaskStatus;
import mapreduce.app.utilities.Enums.TaskType;
import mapreduce.app.utilities.Exceptions.UnknownJobException;

public class JobCoordinator {

    private static final int MAX_SEQUENCE = 4;
    
    private final JobCoordinatorManager manager;
    private final MapResultRepo mapResultRepo;
    private final TaskGenerator taskGenerator;
    private final JobRepo jobRepo;
    private final Long jobId;
    private final TaskScheduler scheduler;
    private final JobEstimateRepo estimateRepo;
    private final EstimateService estimateService;

    public JobCoordinator(JobCoordinatorManager manager, MapResultRepo mapResultRepo, TaskGenerator taskGenerator, JobRepo jobRepo, Long jobId, TaskScheduler scheduler, JobEstimateRepo estimateRepo, EstimateService estimateService) { 
        this.manager = manager;
        this.mapResultRepo = mapResultRepo;
        this.taskGenerator = taskGenerator;
        this.jobRepo = jobRepo;
        this.jobId = jobId;
        this.scheduler = scheduler;
        this.estimateRepo = estimateRepo;
        this.estimateService = estimateService;
    }

    public void poll() {
        Job job = jobRepo.findById(jobId).orElseThrow(() -> new UnknownJobException("No such job by id: " + jobId));
        if(job.getStatus() == JobStatus.CANCELLED) {manager.cancelJob(job);}
        if(Objects.equals(job.getCompletedTasks(), job.getTotalTasks())) {manager.completeJob(job); return;}

        List<Long> sequences = mapResultRepo.getAllUnclaimedSequencesByJob(jobId);
        List<List<Long>> resultSequences = buildSubsequenceRanges(sequences);

        if(resultSequences.isEmpty()) {
            for(Long sequence : sequences) { 
                mapResultRepo.updateClaimsToClaimed(jobId, sequence, sequence);
                resultSequences.add(List.of(sequence, sequence));
            }
        } else { 
            for(List<Long> sequence : resultSequences) { 
                mapResultRepo.updateClaimsToClaimed(jobId, sequence.getFirst(), sequence.getLast());
            }
        }
        taskGenerator.generateReduceTasks(resultSequences, job);   
    }
    public void taskCountAndGeneration(List<Task> tasks) { 
        List<Task> toRunMap = new ArrayList<>();
        List<Task> toRunReduce = new ArrayList<>();
        long runCount = 0;
        long doneCount = 0;
        long totalMillis = 0;
        long doneMapCount = 0;
        Job job = jobRepo.findById(jobId).orElseThrow(() -> new UnknownJobException("No such job by id: " + jobId));
        double time = estimateRepo.findEstimateByJob(job).orElseThrow(() -> new UnknownJobException("No such estimate by job id: " + jobId));
        List<Task> badPerformance = new ArrayList<>();
        for(Task task : tasks) { 
            if (task.getStatus() == TaskStatus.CREATED || task.getStatus() == TaskStatus.RUNNING) {
                runCount++;
                if(task.getTaskType() == TaskType.MAP) toRunMap.add(task);
                else toRunReduce.add(task);
            }
            else if(task.getStatus() == TaskStatus.COMPLETED) {
                doneCount++; 
                if(task.getTaskType() == TaskType.MAP) { 
                    doneMapCount++;
                    long millis = Duration.between(task.getStartedAt(), task.getCompletedAt()).toMillis();
                    totalMillis += millis;
                    if(millis >= time * 3) {
                        Task duplicate = new Task(task);
                        badPerformance.add(duplicate);
                    }
                }
            }
        }
        double average = totalMillis / doneMapCount;
        long total = tasks.size();
        job.setTotalTasks(total);
        job.setCompletedTasks(doneCount);
        job.setFailedTasks(total - doneCount - runCount);
        double potentialTime = estimateService.

        jobRepo.save(job);
        scheduler.pushMapTasks(badPerformance);
        scheduler.pushMapTasks(toRunMap);
        scheduler.pushMapTasks(toRunReduce);
    }

    private List<List<Long>> buildSubsequenceRanges(List<Long> sequences) { 
        List<List<Long>> result = new ArrayList<>();

        if(sequences == null || sequences.isEmpty()) {return result;}

        Collections.sort(sequences);
        
        long start = sequences.get(0);
        long end = sequences.get(0);
        long len = 1;

        for(int i =1; i < sequences.size(); i++) { 
            long current = sequences.get(i);
            if(current == end + 1 && len < MAX_SEQUENCE) {end = current; len ++;}
            else {
                result.add(Arrays.asList(start, end));
                len = 1;
                start = current;
                end = start;
            }
        }

        result.add(Arrays.asList(start, end));
        return result;
    }

}

