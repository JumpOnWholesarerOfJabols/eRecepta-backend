package edu.pk.jawolh.erecepta.patientrecordservice.grpc;

import com.example.demo.codegen.types.PatientInfo;
import edu.pk.jawolh.erecepta.common.record.proto.GetPatientRecordReply;
import edu.pk.jawolh.erecepta.common.record.proto.GetPatientRecordRequest;
import edu.pk.jawolh.erecepta.common.record.proto.PatientRecordServiceGrpc;
import edu.pk.jawolh.erecepta.patientrecordservice.service.PatientRecordService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrpcServerService extends PatientRecordServiceGrpc.PatientRecordServiceImplBase {
    private final PatientRecordService service;

    @Override
    public void getPatientRecord(GetPatientRecordRequest request, StreamObserver<GetPatientRecordReply> responseObserver) {
        PatientInfo patientInfo = service.getPatientInfo(UUID.fromString(request.getPatientId()));

        GetPatientRecordReply reply = GetPatientRecordReply.newBuilder()
                .addAllAllergies(patientInfo.getAllergies())
                .addAllChronicDiseases(patientInfo.getChronicDiseases())
                .addAllMedications(patientInfo.getMedications())
                .setHeight(patientInfo.getHeight())
                .setWeight(patientInfo.getWeight())
                .setBloodType(patientInfo.getBloodType().name())
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
