package mapreduce.app.utilities.Interfaces;

import mapreduce.app.entities.Task;
import mapreduce.app.utilities.Enums.JobType;

public interface TaskService {
    
    void executeMapTask();
    JobType getJobType();
    Task getTask();
    void executeReduceTask();


}
