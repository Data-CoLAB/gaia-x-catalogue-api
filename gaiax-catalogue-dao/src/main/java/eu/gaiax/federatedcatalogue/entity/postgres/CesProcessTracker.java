package eu.gaiax.federatedcatalogue.entity.postgres;

import com.smartsensesolutions.java.commons.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ces_process_tracker")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CesProcessTracker implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;
    @Column(name = "ces_id", nullable = false, unique = true)
    private String cesId;
    @Column(name = "status", nullable = false)
    private Long status;
    @Column(name = "reason")
    private String reason;
    @Column(name = "credential")
    private String credential;
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Date createdAt = new Date();
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Date updateAt = new Date();
}
