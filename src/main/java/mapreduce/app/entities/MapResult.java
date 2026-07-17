package mapreduce.app.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="map_result", indexes = {
    @Index(name="idx_map_result_job_claimed", columnList = "job_id, claimed"),
    @Index(name="idx_map_result_job_sequence", columnList = "job_id, sequence")
})
@Getter
@Setter
@NoArgsConstructor
public class MapResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(nullable = false, name="job_id")
    private Job job;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="task_id", nullable = false)
    private Task task;

    @Column(nullable=false, unique=true)
    private Long sequence;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private boolean claimed;

    @Column(nullable = false)
    private Instant createdAt;
}
