package mapreduce.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobEstimateRepo extends JpaRepository<mapreduce.app.entities.JobEstimate, Long> {
    
}
