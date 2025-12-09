package edu.pk.jawolh.erecepta.medicationservice.grpc;

import edu.pk.jawolh.erecepta.common.medication.proto.MedicationExistsReply;
import edu.pk.jawolh.erecepta.common.medication.proto.MedicationExistsRequest;
import edu.pk.jawolh.erecepta.common.medication.proto.MedicationServiceGrpc;
import edu.pk.jawolh.erecepta.medicationservice.repository.MedicationRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrpcService extends MedicationServiceGrpc.MedicationServiceImplBase {

    private final MedicationRepository medicationRepository;

    @Override
    public void medicationExists(MedicationExistsRequest request,
                                 StreamObserver<MedicationExistsReply> responseObserver) {

        boolean exists = isValidUuid(request.getMedicationId())
                && medicationRepository.existsById(UUID.fromString(request.getMedicationId()));

        MedicationExistsReply reply = MedicationExistsReply.newBuilder()
                .setMedicationExists(exists)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    private boolean isValidUuid(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
