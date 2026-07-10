package mapreduce.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import mapreduce.app.entities.ReduceResult;

public interface ReduceResultRepo extends JpaRepository<ReduceResult, Long>{
    
}
