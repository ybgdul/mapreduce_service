package mapreduce.app.utilities.Events;

public record JobProgressEvent(Long jobId, int progressPercentage) {
    
}
