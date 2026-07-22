package mapreduce.app.utilities.DTOs;

public record JobContext(long inputSize, long remainingTasks, int threadPoolSize, long runningTasks, double AverageWorkingTime) {
    
}
