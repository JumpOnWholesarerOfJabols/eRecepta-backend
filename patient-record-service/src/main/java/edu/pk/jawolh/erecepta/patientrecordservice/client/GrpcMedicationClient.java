package edu.pk.jawolh.erecepta.patientrecordservice.client;

import edu.pk.jawolh.erecepta.common.medication.proto.MedicationExistsReply;
import edu.pk.jawolh.erecepta.common.medication.proto.MedicationExistsRequest;
import edu.pk.jawolh.erecepta.common.medication.proto.MedicationServiceGrpc;
import edu.pk.jawolh.erecepta.common.user.proto.UserExistsGrpc;
import edu.pk.jawolh.erecepta.common.user.proto.UserExistsReply;
import edu.pk.jawolh.erecepta.common.user.proto.UserExistsRequest;
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
}
