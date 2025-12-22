package edu.pk.jawolh.erecepta.medicationservice.grpc;

import edu.pk.jawolh.erecepta.common.medication.proto.*;
import edu.pk.jawolh.erecepta.medicationservice.model.DrugInteractionId;
import edu.pk.jawolh.erecepta.medicationservice.model.Medication;
import edu.pk.jawolh.erecepta.medicationservice.repository.DrugInteractionRepository;
import edu.pk.jawolh.erecepta.medicationservice.repository.MedicationRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrpcServerService extends MedicationServiceGrpc.MedicationServiceImplBase {

    private final MedicationRepository medicationRepository;
    private final DrugInteractionRepository interactionRepository;

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
    public void getMedicationDetails(GetMedicationDetailsRequest request, StreamObserver<GetMedicationDetailsReply> responseObserver) {
        Optional<Medication> medOpt = medicationRepository.findById(UUID.fromString(request.getMedicationId()));
        GetMedicationDetailsReply.Builder builder = GetMedicationDetailsReply.newBuilder();

        if (medOpt.isPresent()) {
            Medication medication = medOpt.get();

            medication.getIngredients().forEach(ingredient -> builder.addIngredients(ingredient.getName()));
            interactionRepository.findInteractionsForMedicationId(UUID.fromString(request.getMedicationId())).forEach(interaction -> {
                DrugInteractionId id = interaction.getId();
                if (id.getMedicationA().equals(medication))
                    builder.addInteractions(id.getMedicationB().getId().toString());
                else builder.addInteractions(id.getMedicationA().getId().toString());
            });
        }

        responseObserver.onNext(builder.build());
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
