package edu.pk.jawolh.erecepta.medicationservice.model;


import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugInteractionId {

    @ManyToOne
    @JoinColumn(name = "medication_a_id")
    private Medication medicationA;

    @ManyToOne
    @JoinColumn(name = "medication_b_id")
    private Medication medicationB;

}
