package mapreduce.app.components;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mapreduce.app.services.NotificationService;
import mapreduce.app.utilities.Events.JobCompletedEvent;

@Component
@RequiredArgsConstructor
public class JobNotificationListener {
    
    private final NotificationService notificationService;

    @EventListener
    public void completed(JobCompletedEvent event) { 
        notificationService.jobCompleted(event.jobId());
    }
}
