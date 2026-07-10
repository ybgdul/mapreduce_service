package mapreduce.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mapreduce.app.entities.Task;
import mapreduce.app.utilities.Enums.TaskStatus;

public interface TaskRepo extends JpaRepository<Task, Long>{
    
    @Query(value = "SELECT * FROM tasks WHERE status = 'ASSIGNED'", nativeQuery = true)
    List<Task> getAllAssignedTasks();

    @Query(value = "SELECT * FROM tasks WHERE status = 'COMPLETED'", nativeQuery = true)
    List<Task> getAllCompletedTasks();
}
