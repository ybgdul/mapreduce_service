package mapreduce.app.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mapreduce.app.utilities.Enums.JobType;
import mapreduce.app.utilities.Enums.TaskStatus;
import mapreduce.app.utilities.Enums.TaskType;


@Entity
@Table(name="tasks", indexes = {
    @Index(name="idx_task_status", columnList = "status"),
    @Index(name="idx_task_job_id", columnList = "job_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Task {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="task_state")
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    @Column(nullable=false)
    private Long sequence;

    private Instant createdAt;

    private Instant startedAt;

    private Instant completedAt;
    
    @Column(nullable = false)
    private long startRange;
    @Column(nullable = false)
    private long endRange;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="job_id", nullable=false)
    private Job job;

    public Long getJobId() { 
        return this.job.getId();
    }

    public Task(Task task) { 
        this.status = task.status;
        this.taskType = task.taskType;
        this.jobType = task.jobType;
        this.sequence = task.sequence;
        this.createdAt = Instant.now();
        this.startedAt = null;
        this.completedAt = null;
        this.startRange = task.startRange;
        this.endRange = task.endRange;
        this.job = task.job;
    }
}
