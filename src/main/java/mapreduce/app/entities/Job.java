package mapreduce.app.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import mapreduce.app.utilities.Enums.JobStatus;
import mapreduce.app.utilities.Enums.JobType;

@Entity
@Table(name="jobs", indexes = {
    @Index(name="idx_job_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class Job {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private JobType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private JobStatus status;

    @Column(nullable=false)
    private Instant createdAt;

    private Instant startedAt;

    private Instant completedAt;

    @Column(nullable=false)
    private Long totalTasks;

    @Column(nullable=false)
    private Long completedTasks;

    @Column(nullable=false)
    private Long failedTasks;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private AppUser user;

    private String errorMessage;

    public Job(JobType type, AppUser user) {
        this.type = type;
        this.user = user;
        this.createdAt = Instant.now();
        this.status = JobStatus.CREATED;
    }
}
