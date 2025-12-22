package edu.pk.jawolh.erecepta.prescriptionservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "medications")
@EqualsAndHashCode(exclude = "medications")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 4)
    private String code;

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private UUID doctorId;

    @Column(nullable = false)
    private UUID visitId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PrescribedMedication> medications;
}
