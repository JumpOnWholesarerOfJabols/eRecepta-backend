package edu.pk.jawolh.erecepta.prescriptionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
