package edu.pk.jawolh.erecepta.med_docs_service.client;

import edu.pk.jawolh.erecepta.common.medication.proto.*;
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

    public GetMedicationDataReply getMedicationData(String UUID) {
        GetMedicationDataRequest request = GetMedicationDataRequest.newBuilder().setMedicationId(UUID).build();
        return stub.getMedicationData(request);
    }
}
