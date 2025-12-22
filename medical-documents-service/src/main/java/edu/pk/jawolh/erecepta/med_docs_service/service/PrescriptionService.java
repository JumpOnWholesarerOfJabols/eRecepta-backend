package edu.pk.jawolh.erecepta.med_docs_service.service;


import com.example.demo.codegen.types.FulfillPrescriptionInput;
import com.example.demo.codegen.types.FulfillResult;
import com.example.demo.codegen.types.IssuePrescriptionInput;
import edu.pk.jawolh.erecepta.med_docs_service.client.GrpcMedicationClient;
import edu.pk.jawolh.erecepta.med_docs_service.client.GrpcUserClient;
import edu.pk.jawolh.erecepta.med_docs_service.exceptions.*;
import edu.pk.jawolh.erecepta.med_docs_service.mappers.PrescriptionMapper;
import edu.pk.jawolh.erecepta.med_docs_service.mappers.PrescriptionStatusMapper;
import edu.pk.jawolh.erecepta.med_docs_service.model.Prescription;
import edu.pk.jawolh.erecepta.med_docs_service.model.PrescriptionFulfillment;
import edu.pk.jawolh.erecepta.med_docs_service.model.PrescriptionStatus;
import edu.pk.jawolh.erecepta.med_docs_service.repository.PrescriptionDAO;
import edu.pk.jawolh.erecepta.med_docs_service.repository.PrescriptionRepository;
import edu.pk.jawolh.erecepta.med_docs_service.utils.CodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final CodeGenerator codeGenerator;
    private final PrescriptionDAO prescriptionDAO;
    private final GrpcUserClient grpcUserClient;
    private final GrpcMedicationClient grpcMedicationClient;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, CodeGenerator codeGenerator, PrescriptionDAO prescriptionDAO, GrpcUserClient grpcUserClient, GrpcMedicationClient grpcMedicationClient) {
        this.prescriptionRepository = prescriptionRepository;
        this.codeGenerator = codeGenerator;
        this.prescriptionDAO = prescriptionDAO;
        this.grpcUserClient = grpcUserClient;
        this.grpcMedicationClient = grpcMedicationClient;
    }

    public com.example.demo.codegen.types.Prescription verifyPrescription(String accessCode, UUID patientIdentifier) {

        Prescription prescription = prescriptionRepository
                .findByPatientIdAndAccessCode(patientIdentifier, accessCode)
                .orElseThrow(()-> new PrescriptionNotFoundException("Prescription not found"));

        return PrescriptionMapper.toDTO(prescription);
    }

    public List<com.example.demo.codegen.types.Prescription> findPrescriptions(
            UUID patientId,
            com.example.demo.codegen.types.PrescriptionStatus status,
            Integer limit,
            Integer offset) {

        edu.pk.jawolh.erecepta.med_docs_service.model.PrescriptionStatus mappedStatus =
                (status != null) ? PrescriptionStatusMapper.fromDTO(status) : null;

        List<Prescription> entities = prescriptionDAO.findPrescriptions(
                patientId,
                mappedStatus,
                limit,
                offset
        );

        return entities.stream()
                .map(PrescriptionMapper::toDTO)
                .toList();
    }

    @Transactional
    public com.example.demo.codegen.types.Prescription issuePrescription(UUID userId, IssuePrescriptionInput input) {

        UUID patientId = UUID.fromString(input.getPatientId());
        UUID medicationId = UUID.fromString(input.getMedicationId());

        if(!grpcUserClient.isDoctor(userId.toString()))
            throw new UnauthorizedException("Only a doctor is allowed to issue a prescription");

        if (!grpcUserClient.isPatient(patientId.toString()))
            throw new UserNotFoundException("Patient not found");

        if (!grpcMedicationClient.isMedication(medicationId.toString()))
            throw new MedicationNotFoundException("Medication not found");

        LocalDate expiresAt = LocalDate.parse(input.getExpiration());

        String prescriptionCode = codeGenerator.generateCode(8);

        Prescription prescription = Prescription
                .builder()
                .accessCode(prescriptionCode)
                .status(PrescriptionStatus.ISSUED)
                .doctorId(userId)
                .patientId(patientId)
                .medicationId(medicationId)
                .totalPackages(input.getQuantity())
                .expiresAt(expiresAt)
                .build();

        Prescription saved = prescriptionRepository.save(prescription);

        return PrescriptionMapper.toDTO(saved);
    }

    @Transactional
    public FulfillResult fulfillPrescription(UUID userId, FulfillPrescriptionInput input) {

        UUID prescriptionId = UUID.fromString(input.getPrescriptionId());

        if(!grpcUserClient.isPharmacist(userId.toString()))
            throw new UnauthorizedException("Only a pharmacist is allowed to fulfill a prescription");

        Prescription fromDb = prescriptionRepository.findById(prescriptionId).orElseThrow(
                        () -> new PrescriptionNotFoundException("Prescription not found"));

        PrescriptionStatus status = fromDb.getStatus();

        if (status == PrescriptionStatus.CANCELLED) {
            throw new PrescriptionCancelledException("Prescription is cancelled");
        }

        if (status == PrescriptionStatus.EXPIRED || fromDb.isExpired())
            throw new PrescriptionExpiredException("Prescription is expired");

        if (status == PrescriptionStatus.FILLED || input.getQuantity() > fromDb.getRemainingPackages()) {
            throw new PrescriptionOverfulfillmentException("Requested quantity exceeds remaining packages");
        }

        Integer filledQuantity = fromDb.getTotalPackages();
        Integer newQuantity = input.getQuantity() + filledQuantity;

        fromDb.setFilledPackages(newQuantity);
        boolean isFilled = fromDb.getRemainingPackages() <= 0;

        if (isFilled)
            fromDb.setStatus(PrescriptionStatus.FILLED);
        else
            fromDb.setStatus(PrescriptionStatus.PARTIALLY_FILLED);

        PrescriptionFulfillment fulfillment = PrescriptionFulfillment.builder()
                .pharmacistId(userId)
                .quantitySold(input.getQuantity())
                .fulfilledAt(LocalDateTime.now())
                .build();

        fromDb.addFulfillment(fulfillment);

        Prescription saved = prescriptionRepository.save(fromDb);

        return FulfillResult.newBuilder()
                .isFullyCompleted(isFilled)
                .updatedPrescription(PrescriptionMapper.toDTO(saved))
                .build();
    }

    @Transactional
    public com.example.demo.codegen.types.Prescription cancelPrescription(UUID userId, UUID prescriptionId, String reason) {

        if(!grpcUserClient.isDoctor(userId.toString()))
            throw new UnauthorizedException("Only a doctor is allowed to cancel a prescription");

        Prescription fromDb = prescriptionRepository.findById(prescriptionId).orElseThrow(
                () -> new PrescriptionNotFoundException("Prescription not found"));

        if (fromDb.getStatus() == PrescriptionStatus.CANCELLED)
            throw new PrescriptionCancelledException("Prescription already cancelled");

        fromDb.setStatus(PrescriptionStatus.CANCELLED);
        fromDb.setCancellationReason(reason);

        Prescription saved = prescriptionRepository.save(fromDb);
        return PrescriptionMapper.toDTO(saved);
    }


}
