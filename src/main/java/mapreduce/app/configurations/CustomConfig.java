package mapreduce.app.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableAspectJAutoProxy
public class CustomConfig {
    
    @Bean 
    public ObjectMapper objectMapper() { 
        return new ObjectMapper();
    }
}
