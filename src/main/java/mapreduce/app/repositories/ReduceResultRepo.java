package mapreduce.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mapreduce.app.entities.Job;
import mapreduce.app.entities.ReduceResult;

public interface ReduceResultRepo extends JpaRepository<ReduceResult, Long>{
    
    public void deleteAllByJob(Job job );

    public List<ReduceResult> findAllByJob(Job job);
}
