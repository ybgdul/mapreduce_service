package mapreduce.app.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mapreduce.app.utilities.Events.JobCompletedEvent;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;

    public void jobCompleted(Long jobId) {
        messagingTemplate.convertAndSend("/topic/jobs/" + jobId, "Job has been completed. Id: " + jobId);
    }

    public void progress(Long jobId, int percent) { 
    }

    public void failed(Long jobId, String reason) {

    }

}
