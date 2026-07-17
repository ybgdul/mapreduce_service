package mapreduce.app.registries;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class UserSessionsRegistry {
    
    private final Map<Long, Set<String>> sessions = new ConcurrentHashMap<>();

    public void register(Long jobId, String sessionId) { 
        sessions.computeIfAbsent(jobId, id -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public void remove(Long jobId, String sessionId) { 
        sessions.getOrDefault(jobId, Set.of()).remove(sessionId);
    }

    public boolean hasAnySessions(Long jobId) {
        return sessions.getOrDefault(jobId, Set.of()).isEmpty();
    }
}
