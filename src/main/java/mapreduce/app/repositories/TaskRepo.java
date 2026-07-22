package mapreduce.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mapreduce.app.entities.Job;
import mapreduce.app.entities.Task;

public interface TaskRepo extends JpaRepository<Task, Long>{
    
    @Query(value = "SELECT * FROM tasks WHERE status = 'CREATED' OR status = 'ASSIGNED' OR status = 'RUNNING' ", nativeQuery = true)
    public List<Task> getAllCreatedRunningAssignedTasks();

    public void deleteAllByJob(Job job );

    @Query(value = "SELECT * FROM tasks WHERE job_id IN :job_ids ", nativeQuery = true)
    public List<Task> getAllTaskBelongToJobId(@Param("job_ids")List<Long> job_ids);

}
