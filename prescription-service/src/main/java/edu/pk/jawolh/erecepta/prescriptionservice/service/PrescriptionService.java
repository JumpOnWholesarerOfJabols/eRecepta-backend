package edu.pk.jawolh.erecepta.prescriptionservice.service;

import com.example.demo.codegen.types.CreatePrescriptionInput;
import com.example.demo.codegen.types.PrescribedMedicationInput;
import edu.pk.jawolh.erecepta.common.visit.enums.VisitStatus;
import edu.pk.jawolh.erecepta.prescriptionservice.client.GrpcMedicationClient;
import edu.pk.jawolh.erecepta.prescriptionservice.client.GrpcPatientRecordClient;
import edu.pk.jawolh.erecepta.prescriptionservice.client.GrpcVisitClient;
import edu.pk.jawolh.erecepta.prescriptionservice.dto.MedicationDetailsDTO;
import edu.pk.jawolh.erecepta.prescriptionservice.dto.PatientRecordDTO;
import edu.pk.jawolh.erecepta.prescriptionservice.dto.VisitDTO;
import edu.pk.jawolh.erecepta.prescriptionservice.exception.*;
import edu.pk.jawolh.erecepta.prescriptionservice.mapper.PrescriptionMapper;
import edu.pk.jawolh.erecepta.prescriptionservice.model.PrescribedMedication;
import edu.pk.jawolh.erecepta.prescriptionservice.model.Prescription;
import edu.pk.jawolh.erecepta.prescriptionservice.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionService {
    private final PrescriptionRepository repository;
    private final GrpcVisitClient visitClient;
    private final GrpcPatientRecordClient recordClient;
    private final GrpcMedicationClient medicationClient;
    private final PrescriptionMapper mapper;

    public List<com.example.demo.codegen.types.Prescription> getPrescriptions(UUID patientId) {
        return repository.findAllByPatientId(patientId).stream().map(mapper::fromEntity).toList();
    }

    public Optional<com.example.demo.codegen.types.Prescription> getPrescriptionByVisitId(UUID userId, UUID visitId) {
        return repository.findByVisitIdAndDoctorIdOrPatientId(visitId, userId, userId).map(mapper::fromEntity);
    }

    @Transactional
    public com.example.demo.codegen.types.Prescription createPrescription(UUID doctorId, CreatePrescriptionInput input) {
        log.info("Creating prescription for doctor {} and visit {}", doctorId, input.getVisitId());
        
        UUID visitId = UUID.fromString(input.getVisitId());
        
        if (repository.existsByVisitId(visitId)) {
            log.warn("Prescription already exists for visit {}", visitId);
            throw new DuplicatePrescriptionException("Prescription already exists for visit " + visitId);
        }
        
        if (!visitClient.checkVisitExists(visitId, doctorId)) {
            log.warn("Visit {} does not exist or does not belong to doctor {}", visitId, doctorId);
            throw new VisitNotFoundException("Visit does not exist or does not belong to doctor");
        }

        VisitDTO visitDTO = visitClient.getVisit(visitId);
        validateVisit(visitDTO);

        PatientRecordDTO recordDTO = recordClient.getPatientRecord(visitDTO.patientId().toString());
        
        List<UUID> medicationIds = input.getMedications().stream()
                .map(m -> UUID.fromString(m.getMedicationId()))
                .toList();
        
        validateMedications(medicationIds, recordDTO, input.getIgnoreAllergies(), input.getIgnoreInteractions());

        Prescription prescription = buildPrescription(doctorId, visitDTO, input);
        
        Prescription saved = repository.save(prescription);
        log.info("Successfully created prescription with ID: {}", saved.getId());
        
        return mapper.fromEntity(saved);
    }

    private void validateVisit(VisitDTO visitDTO) {
        if (visitDTO.visitStatus().equals(VisitStatus.CANCELLED)) {
            log.warn("Cannot create prescription for cancelled visit {}", visitDTO.id());
            throw new VisitCancelledException("Cannot create prescription for cancelled visit");
        }

        if (visitDTO.visitTime().isAfter(LocalDateTime.now())) {
            log.warn("Cannot create prescription for future visit {}", visitDTO.id());
            throw new FutureVisitException("Visit has not happened yet");
        }
    }

    private void validateMedications(List<UUID> medicationIds, PatientRecordDTO recordDTO, 
                                      boolean ignoreAllergies, boolean ignoreInteractions) {
        Set<String> currentMedicationIds = new HashSet<>(recordDTO.medications());
        Set<String> prescribedMedicationIds = medicationIds.stream()
                .map(UUID::toString)
                .collect(Collectors.toSet());
        
        for (UUID medicationId : medicationIds) {
            String medIdStr = medicationId.toString();
            
            if (!medicationClient.isMedication(medIdStr)) {
                log.warn("Medication {} does not exist", medIdStr);
                throw new MedicationNotFoundException("Medication with ID " + medIdStr + " does not exist");
            }

            MedicationDetailsDTO details = medicationClient.getMedicationDetails(medIdStr);

            if (!ignoreAllergies) {
                for (String ingredient : details.ingredients()) {
                    if (recordDTO.allergies().contains(ingredient)) {
                        log.warn("Patient is allergic to ingredient {} in medication {}", ingredient, medIdStr);
                        throw new PatientAllergyException(
                            String.format("Patient is allergic to %s in medication %s", ingredient, medIdStr)
                        );
                    }
                }
            }

            if (!ignoreInteractions) {
                for (String interactingMedId : details.interactions()) {
                    if (prescribedMedicationIds.contains(interactingMedId) || 
                        currentMedicationIds.contains(interactingMedId)) {
                        log.warn("Medication {} interacts with {}", medIdStr, interactingMedId);
                        throw new DrugInteractionException(
                            String.format("Medication %s interacts with medication %s", medIdStr, interactingMedId)
                        );
                    }
                }
            }
        }
    }

    private Prescription buildPrescription(UUID doctorId, VisitDTO visitDTO, CreatePrescriptionInput input) {
        return Prescription.builder()
                .doctorId(doctorId)
                .patientId(visitDTO.patientId())
                .visitId(visitDTO.id())
                .medications(input.getMedications().stream()
                        .map(im -> PrescribedMedication.builder()
                                .medicationId(UUID.fromString(im.getMedicationId()))
                                .usageNotes(im.getUsageNotes())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
