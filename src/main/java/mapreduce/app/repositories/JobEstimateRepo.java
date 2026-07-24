package mapreduce.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mapreduce.app.entities.Job;

public interface JobEstimateRepo extends JpaRepository<mapreduce.app.entities.JobEstimate, Long> {
    
    public Optional<Double> findEstimateByJob(Job job);
}
