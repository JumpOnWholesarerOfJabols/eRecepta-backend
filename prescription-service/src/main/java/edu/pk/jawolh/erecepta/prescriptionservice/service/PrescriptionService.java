package edu.pk.jawolh.erecepta.prescriptionservice.service;

import edu.pk.jawolh.erecepta.common.user.enums.UserRole;
import edu.pk.jawolh.erecepta.common.visit.enums.VisitStatus;
import edu.pk.jawolh.erecepta.prescriptionservice.client.GrpcMedicationClient;
import edu.pk.jawolh.erecepta.prescriptionservice.client.GrpcPatientRecordClient;
import edu.pk.jawolh.erecepta.prescriptionservice.client.GrpcVisitClient;
import edu.pk.jawolh.erecepta.prescriptionservice.dto.PrescribedMedicationRequest;
import edu.pk.jawolh.erecepta.prescriptionservice.dto.PrescriptionRequest;
import edu.pk.jawolh.erecepta.prescriptionservice.dto.VisitDTO;
import edu.pk.jawolh.erecepta.prescriptionservice.exception.AllergyConflictException;
import edu.pk.jawolh.erecepta.prescriptionservice.exception.InvalidVisitStateException;
import edu.pk.jawolh.erecepta.prescriptionservice.exception.PrescriptionNotFoundException;
import edu.pk.jawolh.erecepta.prescriptionservice.exception.UnauthorizedAccessException;
import edu.pk.jawolh.erecepta.prescriptionservice.model.PrescribedMedication;
import edu.pk.jawolh.erecepta.prescriptionservice.model.Prescription;
import edu.pk.jawolh.erecepta.prescriptionservice.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final GrpcVisitClient visitClient;
    private final GrpcPatientRecordClient patientRecordClient;
    private final GrpcMedicationClient medicationClient;

    @Transactional
    public Prescription createPrescription(PrescriptionRequest request, UUID doctorId) {
        log.debug("Creating prescription for visit {} by doctor {}", request.visitId(), doctorId);

        // 1. Fetch visit
        VisitDTO visit = visitClient.getVisit(request.visitId());

        // 2. Validate that the visit belongs to the authenticated doctor
        if (!visit.doctorId().equals(doctorId)) {
            throw new UnauthorizedAccessException("Doctor does not own this visit");
        }

        // 3. Validate that the visit has already occurred
        if (visit.visitTime().isAfter(LocalDateTime.now())) {
            throw new InvalidVisitStateException("Cannot create prescription for future visit");
        }

        // 4. Validate that the visit was not cancelled
        if (visit.visitStatus() == VisitStatus.CANCELLED) {
            throw new InvalidVisitStateException("Cannot create prescription for cancelled visit");
        }

        // 5. Fetch patient allergies
        List<String> patientAllergies = patientRecordClient.getPatientAllergies(visit.patientId());
        log.debug("Patient allergies: {}", patientAllergies);

        // 6. Check for allergy conflicts
        List<String> conflictingIngredients = new ArrayList<>();
        for (PrescribedMedicationRequest medRequest : request.medications()) {
            List<String> medicationIngredients = medicationClient.getMedicationIngredients(medRequest.medicationId());
            log.debug("Medication {} ingredients: {}", medRequest.medicationId(), medicationIngredients);

            // Check if any ingredient matches patient allergies (case-insensitive)
            for (String ingredient : medicationIngredients) {
                for (String allergy : patientAllergies) {
                    if (ingredient.equalsIgnoreCase(allergy)) {
                        conflictingIngredients.add(ingredient);
                    }
                }
            }
        }

        if (!conflictingIngredients.isEmpty()) {
            throw new AllergyConflictException(
                    "Patient is allergic to the following ingredients: " + String.join(", ", conflictingIngredients),
                    conflictingIngredients
            );
        }

        // 7. Create prescription
        Prescription prescription = Prescription.builder()
                .patientId(visit.patientId())
                .doctorId(visit.doctorId())
                .visitId(visit.id())
                .code(generatePrescriptionCode())
                .build();

        // 8. Create prescribed medications
        Set<PrescribedMedication> prescribedMedications = request.medications().stream()
                .map(medRequest -> {
                    PrescribedMedication med = new PrescribedMedication();
                    med.setPrescription(prescription);
                    med.setMedicationId(medRequest.medicationId());
                    med.setUsageNotes(medRequest.usageNotes());
                    return med;
                })
                .collect(Collectors.toSet());

        prescription.setMedications(prescribedMedications);

        // 9. Save and return
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        log.info("Created prescription {} for visit {}", savedPrescription.getId(), visit.id());
        return savedPrescription;
    }

    public Prescription getPrescription(UUID prescriptionId) {
        log.debug("Fetching prescription {}", prescriptionId);
        return prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with id: " + prescriptionId));
    }

    public List<Prescription> getAllPrescriptions() {
        log.debug("Fetching all prescriptions");
        return prescriptionRepository.findAll();
    }

    @Transactional
    public void deletePrescription(UUID prescriptionId, UUID userId, UserRole userRole) {
        log.debug("Deleting prescription {} by user {} with role {}", prescriptionId, userId, userRole);

        // Validate that user is admin
        if (userRole != UserRole.ADMINISTRATOR) {
            throw new UnauthorizedAccessException("Only administrators can delete prescriptions");
        }

        if (!prescriptionRepository.existsById(prescriptionId)) {
            throw new PrescriptionNotFoundException("Prescription not found with id: " + prescriptionId);
        }

        prescriptionRepository.deleteById(prescriptionId);
        log.info("Deleted prescription {}", prescriptionId);
    }

    private String generatePrescriptionCode() {
        // Generate a random 4-character alphanumeric code
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}
