package mapreduce.app.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mapreduce.app.utilities.Enums.TaskState;

@Entity
@Table(name="tasks")
@Getter
@Setter
@NoArgsConstructor
public class Task {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="task_state")
    private TaskState taskState;

    @Column(nullable=false)
    private String type;

    private boolean isDone;

    @Column(nullable=false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant assignedAt;

    @Column(nullable=false)
    private Instant updatedAt;
    
    @ManyToOne
    @JoinColumn(name="job_id", nullable=false)
    private Job job;
    
    @PreUpdate
    public void preUpdate() { 
        this.updatedAt = Instant.now();
    }

    public Task(String type, String payload, Instant runAt ) {
        this.type = type;
        this.payload = payload;
        this.runAt = runAt;
        this.jobStatus = JobStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.processingAt = Instant.now();
    }
}
