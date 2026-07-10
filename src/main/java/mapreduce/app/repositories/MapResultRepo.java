package mapreduce.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mapreduce.app.entities.MapResult;

public interface MapResultRepo extends JpaRepository<MapResult, Long>{
    
    @Query(value="SELECT m.sequence FROM map_result m WHERE m.job_id = :job_id", nativeQuery=true)
    List<Long> getAllSequencesByJob(@Param("job_id") Long jobId);
}
