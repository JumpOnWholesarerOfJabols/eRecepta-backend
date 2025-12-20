package edu.pk.jawolh.erecepta.prescriptionservice.client;

import edu.pk.jawolh.erecepta.common.medication.proto.*;
import edu.pk.jawolh.erecepta.prescriptionservice.dto.MedicationDetailsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcMedicationClient {
    private final MedicationServiceGrpc.MedicationServiceBlockingStub stub;

    public boolean isMedication(String UUID) {
        MedicationExistsRequest request = MedicationExistsRequest.newBuilder().setMedicationId(UUID).build();
        MedicationExistsReply response = stub.medicationExists(request);
        return response.getMedicationExists();
    }

    public MedicationDetailsDTO getMedicationDetails(String id) {
        GetMedicationDetailsRequest request = GetMedicationDetailsRequest.newBuilder().setMedicationId(id).build();
        GetMedicationDetailsReply response = stub.getMedicationDetails(request);

        return new MedicationDetailsDTO(response.getIngredientsList(), response.getInteractionsList());
    }
}
