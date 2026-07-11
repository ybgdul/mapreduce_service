package mapreduce.app.utilities.POJOs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import mapreduce.app.components.TaskGenerator;
import mapreduce.app.entities.Job;
import mapreduce.app.repositories.JobRepo;
import mapreduce.app.repositories.MapResultRepo;

public class JobCoordinator {

    private static final int MAX_SEQUENCE = 4;
    
    private final MapResultRepo mapResultRepo;
    private final TaskGenerator taskGenerator;
    private final JobRepo jobRepo;
    private final Long jobId;

    public JobCoordinator(MapResultRepo mapResultRepo, TaskGenerator taskGenerator, JobRepo jobRepo, Long jobId) { 
        this.mapResultRepo = mapResultRepo;
        this.taskGenerator = taskGenerator;
        this.jobRepo = jobRepo;
        this.jobId = jobId;
    }

    public void poll() { 
        List<List<Long>> resultSequences = buildSubsequenceRanges(mapResultRepo.getAllUnclaimedSequencesByJob(jobId));
        for(List<Long> sequence : resultSequences) { 
            mapResultRepo.updateClaimsToClaimed(jobId, sequence.getFirst(), sequence.getLast());
        }
        Job job = jobRepo.findById(jobId).orElseThrow(() -> new NoSuchElementException("No such job by id: " + jobId));
        taskGenerator.generateReduceTasks(resultSequences, job);   
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

