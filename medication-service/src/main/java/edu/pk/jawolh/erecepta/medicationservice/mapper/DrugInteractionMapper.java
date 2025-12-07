package edu.pk.jawolh.erecepta.medicationservice.mapper;

import edu.pk.jawolh.erecepta.medicationservice.model.DrugInteraction;
import com.example.demo.codegen.types.RiskLevel;
import java.util.UUID;

public class DrugInteractionMapper {

    public static com.example.demo.codegen.types.DrugInteraction toDTO(
            DrugInteraction domainInteraction,
            UUID targetMedicationId) {

        if (domainInteraction == null) {
            return null;
        }

        UUID medicationAId = domainInteraction.getId().getMedicationA().getId();
        UUID medicationBId = domainInteraction.getId().getMedicationB().getId();

        UUID interactionPartnerId;

        if (targetMedicationId.equals(medicationAId)) {
            interactionPartnerId = medicationBId;
        }
        else {
            interactionPartnerId = medicationAId;
        }

        return com.example.demo.codegen.types.DrugInteraction.newBuilder()
                .targetMedicationId(interactionPartnerId.toString())
                .riskLevel(RiskLevel.valueOf(domainInteraction.getRiskLevel().name()))
                .effect(domainInteraction.getEffect())
                .build();
    }
}