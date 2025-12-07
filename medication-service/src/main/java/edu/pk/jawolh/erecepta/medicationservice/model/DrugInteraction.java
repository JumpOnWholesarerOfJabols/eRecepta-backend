package edu.pk.jawolh.erecepta.medicationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrugInteraction {
    @EmbeddedId
    private DrugInteractionId id;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;
    private String effect;
}
