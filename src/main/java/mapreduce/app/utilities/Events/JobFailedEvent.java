package mapreduce.app.utilities.Events;

public record JobFailedEvent(Long jobId, String reason) {
    
}
