package edu.pk.jawolh.erecepta.prescriptionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescribedMedication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private UUID id;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Prescription prescription;

    @Column(nullable = false)
    private UUID medicationId;

    @Column(nullable = false)
    private String usageNotes;
}
