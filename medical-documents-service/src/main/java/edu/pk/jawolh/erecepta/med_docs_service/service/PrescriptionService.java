package edu.pk.jawolh.erecepta.med_docs_service.service;


import com.example.demo.codegen.types.FulfillPrescriptionInput;
import com.example.demo.codegen.types.FulfillResult;
import com.example.demo.codegen.types.IssuePrescriptionInput;
import edu.pk.jawolh.erecepta.med_docs_service.exceptions.PrescriptionCancelledException;
import edu.pk.jawolh.erecepta.med_docs_service.exceptions.PrescriptionExpiredException;
import edu.pk.jawolh.erecepta.med_docs_service.exceptions.PrescriptionNotFoundException;
import edu.pk.jawolh.erecepta.med_docs_service.exceptions.PrescriptionOverfulfillmentException;
import edu.pk.jawolh.erecepta.med_docs_service.mappers.PrescriptionMapper;
import edu.pk.jawolh.erecepta.med_docs_service.model.Prescription;
import edu.pk.jawolh.erecepta.med_docs_service.model.PrescriptionFulfillment;
import edu.pk.jawolh.erecepta.med_docs_service.model.PrescriptionStatus;
import edu.pk.jawolh.erecepta.med_docs_service.repository.PrescriptionRepository;
import edu.pk.jawolh.erecepta.med_docs_service.utils.CodeGenerator;
import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final CodeGenerator codeGenerator;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, CodeGenerator codeGenerator) {
        this.prescriptionRepository = prescriptionRepository;
        this.codeGenerator = codeGenerator;
    }

    public com.example.demo.codegen.types.Prescription verifyPrescription(String accessCode, UUID patientIdentifier) {

        Prescription prescription = prescriptionRepository
                .findByPatientIdAndAccessCode(patientIdentifier, accessCode)
                .orElseThrow(()-> new PrescriptionNotFoundException("Prescription not found"));

        return PrescriptionMapper.toDTO(prescription);
    }

    @Transient
    public com.example.demo.codegen.types.Prescription issuePrescription(IssuePrescriptionInput input) {
        //TODO communication with identity and medication service to verify if patient and medicine exists

        UUID doctorId = UUID.fromString(input.getDoctorId());
        UUID patientId = UUID.fromString(input.getPatientId());
        UUID medicationId = UUID.fromString(input.getMedicationId());

        LocalDate expiresAt = LocalDate.parse(input.getExpiration());

        String prescriptionCode = codeGenerator.generateCode(8);

        Prescription prescription = Prescription
                .builder()
                .accessCode(prescriptionCode)
                .status(PrescriptionStatus.ISSUED)
                .doctorId(doctorId)
                .patientId(patientId)
                .medicationId(medicationId)
                .totalPackages(input.getQuantity())
                .expiresAt(expiresAt)
                .build();

        Prescription saved = prescriptionRepository.save(prescription);

        return PrescriptionMapper.toDTO(saved);
    }

    @Transactional
    public FulfillResult fulfillPrescription(FulfillPrescriptionInput input) {

        //todo pharmacist verification
        UUID pharmacistId = UUID.fromString(input.getPharmacistId());
        UUID prescriptionId = UUID.fromString(input.getPrescriptionId());

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
                .pharmacistId(pharmacistId)
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
}
