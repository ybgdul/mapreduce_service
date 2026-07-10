package mapreduce.app.services;

import mapreduce.app.entities.Task;
import mapreduce.app.utilities.Enums.JobType;
import mapreduce.app.utilities.Interfaces.TaskService;

public class CountWordsTaskService implements TaskService{


    @Override
    public JobType getJobType() {
        return JobType.COUNT_WORDS;
    }

    @Override
    public Task getTask() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTask'");
    }

    @Override
    public void executeMapTask() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeMapTask'");
    }

    @Override
    public void executeReduceTask() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeReduceTask'");
    }
    
}
