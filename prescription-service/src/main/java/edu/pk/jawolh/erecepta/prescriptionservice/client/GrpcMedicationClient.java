package edu.pk.jawolh.erecepta.prescriptionservice.client;

import edu.pk.jawolh.erecepta.common.medication.proto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrpcMedicationClient {
    private final MedicationServiceGrpc.MedicationServiceBlockingStub stub;

    public boolean isMedication(String UUID) {
        MedicationExistsRequest request = MedicationExistsRequest.newBuilder().setMedicationId(UUID).build();
        MedicationExistsReply response = stub.medicationExists(request);
        return response.getMedicationExists();
    }

    public List<String> getMedicationIngredients(UUID medicationId) {
        GetMedicationIngredientsRequest request = GetMedicationIngredientsRequest.newBuilder()
                .setMedicationId(medicationId.toString())
                .build();
        GetMedicationIngredientsReply response = stub.getMedicationIngredients(request);
        return response.getIngredientsList().stream()
                .map(IngredientDTO::getName)
                .toList();
    }
}
