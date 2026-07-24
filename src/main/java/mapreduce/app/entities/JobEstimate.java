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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="job_estimate", indexes={
    @Index(name="idx_job_estimate_job_id", columnList="job_id")
})
@Getter
@Setter
@NoArgsConstructor
public class JobEstimate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(nullable = false, name="job_id", unique=true)
    private Job job;

    @Column(nullable=false)
    private Double estimate;

    @Column(nullable=false)
    private Instant createdAt;

    @Version
    private Long version;
}
