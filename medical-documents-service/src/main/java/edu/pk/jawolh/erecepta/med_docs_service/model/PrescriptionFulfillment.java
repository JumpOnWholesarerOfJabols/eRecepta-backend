package edu.pk.jawolh.erecepta.med_docs_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionFulfillment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID pharmacistId;

    @Column(nullable = false)
    private Integer quantitySold;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fulfilledAt;
}
