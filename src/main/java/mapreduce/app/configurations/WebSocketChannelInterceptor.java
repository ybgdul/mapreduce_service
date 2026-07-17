package mapreduce.app.configurations;

import java.security.Principal;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mapreduce.app.entities.AppUser;
import mapreduce.app.repositories.JobRepo;

@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {
    
    private final JobRepo jobRepo;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) { 
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) { 
            String destination = accessor.getDestination();

            Long jobId = Long.parseLong(destination.substring(12));

            AppUser appUser = jobRepo.findById(jobId).orElseThrow(() -> new AccessDeniedException("Job by id not found")).getUser();

            Authentication auth = (Authentication) accessor.getUser();
            User user = (User) auth.getPrincipal();

            if(!user.getUsername().equals(appUser.getUsername())) {throw new AccessDeniedException("User has no access to the job");}
        }

        return message;
    }

}
