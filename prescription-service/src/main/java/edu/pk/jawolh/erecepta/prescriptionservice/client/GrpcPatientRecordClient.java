package edu.pk.jawolh.erecepta.prescriptionservice.client;

import edu.pk.jawolh.erecepta.common.record.proto.GetPatientRecordReply;
import edu.pk.jawolh.erecepta.common.record.proto.GetPatientRecordRequest;
import edu.pk.jawolh.erecepta.common.record.proto.PatientRecordServiceGrpc;
import edu.pk.jawolh.erecepta.prescriptionservice.dto.PatientRecordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrpcPatientRecordClient {
    private final PatientRecordServiceGrpc.PatientRecordServiceBlockingStub stub;

    public PatientRecordDTO getPatientRecord(String patientId) {
        GetPatientRecordRequest getPatientRecordRequest = GetPatientRecordRequest.newBuilder().setPatientId(patientId).build();
        GetPatientRecordReply response = stub.getPatientRecord(getPatientRecordRequest);

        return new PatientRecordDTO(response.getAllergiesList(), response.getMedicationsList());
    }
}
