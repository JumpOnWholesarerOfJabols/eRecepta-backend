package edu.pk.jawolh.erecepta.medicationservice.grpc;

import edu.pk.jawolh.erecepta.common.medication.proto.*;
import edu.pk.jawolh.erecepta.medicationservice.mapper.MedicationMapper;
import edu.pk.jawolh.erecepta.medicationservice.repository.MedicationRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional(readOnly = true)
    public void getMedicationData(GetMedicationDataRequest request, StreamObserver<GetMedicationDataReply> responseObserver) {

        if (!isValidUuid(request.getMedicationId())) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid UUID format")
                    .asRuntimeException());
            return;
        }

        medicationRepository.findById(UUID.fromString(request.getMedicationId()))
                .map(MedicationMapper::toProto)
                .ifPresentOrElse(
                        reply -> {
                            responseObserver.onNext(reply);
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(Status.NOT_FOUND
                                .withDescription("Medication with ID " + request.getMedicationId() + " not found")
                                .asRuntimeException())
                );
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
