package mapreduce.app.services;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

public class RateLimitingService {
    
    private final Map<String, Bucket> map = new ConcurrentHashMap<>();

    private Bucket createNewBucket() { 
        Bandwidth bandwidth = Bandwidth.builder().capacity(10).refillIntervally(1, Duration.ofSeconds(60)).build();
        return Bucket.builder().addLimit(bandwidth).build();
    }

    public boolean tryConsume(String key) { 
        Bucket bucket = map.computeIfAbsent(key, k-> createNewBucket());
        return bucket.tryConsume(1);
    }
}
