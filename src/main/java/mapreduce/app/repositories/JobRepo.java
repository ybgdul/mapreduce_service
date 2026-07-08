package mapreduce.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import mapreduce.app.entities.Job;

public interface JobRepo extends JpaRepository<Job, Long>{
    
}
