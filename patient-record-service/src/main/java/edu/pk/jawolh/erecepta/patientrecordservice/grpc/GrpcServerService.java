package edu.pk.jawolh.erecepta.patientrecordservice.grpc;

import com.example.demo.codegen.types.BloodType;
import com.example.demo.codegen.types.PatientInfo;
import edu.pk.jawolh.erecepta.common.record.proto.GetPatientRecordReply;
import edu.pk.jawolh.erecepta.common.record.proto.GetPatientRecordRequest;
import edu.pk.jawolh.erecepta.common.record.proto.PatientRecordServiceGrpc;
import edu.pk.jawolh.erecepta.patientrecordservice.service.PatientRecordService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrpcServerService extends PatientRecordServiceGrpc.PatientRecordServiceImplBase {
    private final PatientRecordService service;

    @Override
    @Transactional(readOnly = true)
    public void getPatientRecord(GetPatientRecordRequest request, StreamObserver<GetPatientRecordReply> responseObserver) {
        PatientInfo patientInfo = service.getPatientInfo(UUID.fromString(request.getPatientId()));
        BloodType bloodType = patientInfo.getBloodType();

        GetPatientRecordReply reply = GetPatientRecordReply.newBuilder()
                .addAllAllergies(patientInfo.getAllergies())
                .addAllChronicDiseases(patientInfo.getChronicDiseases())
                .addAllMedications(patientInfo.getMedications())
                .setHeight(patientInfo.getHeight())
                .setWeight(patientInfo.getWeight())
                .setBloodType(bloodType == null ? "" : bloodType.name())
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
