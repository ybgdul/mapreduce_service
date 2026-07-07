package mapreduce.app.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="jobs")
@Getter
@Setter
@NoArgsConstructor
public class Job {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String type;

    @Column(nullable=false)
    private Instant createdAt;

    @Column(nullable=false)
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private AppUser user;

    @OneToMany(mappedBy="job", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Task> tasks;

    public Job(String type, AppUser user) {
        this.type = type;
        this.user = user;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.tasks = new ArrayList<>();
    }

    @PreUpdate
    public void preUpdate() { 
        this.updatedAt = Instant.now();
    }
}
