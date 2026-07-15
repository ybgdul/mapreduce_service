package mapreduce.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mapreduce.app.entities.Job;

public interface JobRepo extends JpaRepository<Job, Long>{
    
    @Query(value="SELECT * FROM job_table WHERE status = 'RUNNING' or status = 'CREATED' or status = ''", nativeQuery=true)
    public List<Job> findAllLeftOutJobs();
}
