package mapreduce.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mapreduce.app.entities.Job;
import mapreduce.app.utilities.Enums.JobStatus;

public interface JobRepo extends JpaRepository<Job, Long>{
    
    @Query(value="SELECT * FROM jobs WHERE status = 'RUNNING' or status = 'CREATED'", nativeQuery=true)
    public List<Job> findAllLeftOutJobs();
}
