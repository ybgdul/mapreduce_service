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
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    private String workerId;

    private String inputReference;
    private String outputReference;

    private Instant createdAt;

    private Instant startedAt;

    private Instant completedAt;
    
    @Column(nullable = false)
    private long startOffset;
    @Column(nullable = false)
    private long endOffset;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="job_id", nullable=false)
    private Job job;

}
