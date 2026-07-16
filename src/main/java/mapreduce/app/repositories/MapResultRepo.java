package mapreduce.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mapreduce.app.entities.Job;
import mapreduce.app.entities.MapResult;

public interface MapResultRepo extends JpaRepository<MapResult, Long>{
    
    @Query(value="SELECT sequence FROM map_result WHERE job_id = :job_id AND claimed = false", nativeQuery=true)
    public List<Long> getAllUnclaimedSequencesByJob(@Param("job_id") Long jobId);

    @Query(value="UPDATE map_result SET claimed = true WHERE job_id = :job_id AND sequence BETWEEN :start AND :end", nativeQuery=true)
    public void updateClaimsToClaimed(@Param("job_id") Long jobId, @Param("start") Long start, @Param("end") Long end);

    @Query(value="SELECT * FROM map_result WHERE job_id = :job_id AND sequence BETWEEN :start AND :end", nativeQuery=true)
    public List<MapResult> getAllResultsBySequenceAndJobId(@Param("job_id") Long job_id, @Param("start") Long start, @Param("end") Long end);

    public void deleteAllByJob(Job job );
}
