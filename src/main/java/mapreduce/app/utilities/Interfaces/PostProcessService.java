package mapreduce.app.utilities.Interfaces;

import mapreduce.app.entities.Task;
import mapreduce.app.utilities.Enums.JobType;
import mapreduce.app.entities.Job;

public interface PostProcessService {
    
    void postProcess(Job job);
    JobType getJobType();
}
