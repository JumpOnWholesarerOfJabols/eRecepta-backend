package edu.pk.jawolh.erecepta.medicationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String ean;

    @Column(nullable = false)
    private String atcCode;

    @Column(nullable = false)
    private String tradeName;

    @Column(nullable = false)
    private String genericName;

    @Column(nullable = false)
    private String manufacturer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicationForm form;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RouteOfAdministration route;

    @Column(nullable = false)
    private String packageSize;

    @Column(nullable = false)
    private boolean requiresPrescription;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "medication_id")
    private List<Ingredient> ingredients;

    @ElementCollection
    private List<String> indications;

    @ElementCollection
    private List<String> sideEffects;
}
