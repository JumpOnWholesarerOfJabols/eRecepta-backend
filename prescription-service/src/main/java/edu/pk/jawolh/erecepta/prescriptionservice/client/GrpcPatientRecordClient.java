package edu.pk.jawolh.erecepta.prescriptionservice.client;

import edu.pk.jawolh.erecepta.common.record.proto.GetAllergiesRequest;
import edu.pk.jawolh.erecepta.common.record.proto.PatientRecordServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrpcPatientRecordClient {
    private final PatientRecordServiceGrpc.PatientRecordServiceBlockingStub stub;

    public List<String> getPatientAllergies(UUID patientId) {
        return stub.getAllergies(GetAllergiesRequest.newBuilder().setPatientId(patientId.toString()).build()).getAllergiesList();
    }
}
