package edu.pk.jawolh.erecepta.med_docs_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 8)
    private String accessCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrescriptionStatus status;

    @Column(nullable = false)
    private UUID doctorId;

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private UUID medicationId;

    @Column(nullable = false)
    private Integer totalPackages;

    @Column(nullable = false)
    @Builder.Default
    private Integer filledPackages = 0;

    @Column(length = 500)
    private String cancellationReason;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "prescription_id", nullable = false)
    @Builder.Default
    private List<PrescriptionFulfillment> fulfillments = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Transient
    public Integer getRemainingPackages() {
        if (totalPackages == null || filledPackages == null) {
            return 0;
        }
        return Math.max(0, totalPackages - filledPackages);
    }

    @Transient
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public void addFulfillment(PrescriptionFulfillment fulfillment) {
        this.fulfillments.add(fulfillment);
        this.filledPackages += fulfillment.getQuantitySold();

        if (this.filledPackages >= this.totalPackages) {
            this.status = PrescriptionStatus.FILLED;
        } else {
            this.status = PrescriptionStatus.PARTIALLY_FILLED;
        }
    }
}
