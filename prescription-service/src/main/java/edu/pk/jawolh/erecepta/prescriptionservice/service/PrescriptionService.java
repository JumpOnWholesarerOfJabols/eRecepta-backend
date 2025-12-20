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
import edu.pk.jawolh.erecepta.prescriptionservice.mapper.PrescriptionMapper;
import edu.pk.jawolh.erecepta.prescriptionservice.model.PrescribedMedication;
import edu.pk.jawolh.erecepta.prescriptionservice.model.Prescription;
import edu.pk.jawolh.erecepta.prescriptionservice.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

    public com.example.demo.codegen.types.Prescription createPrescription(UUID doctorId, CreatePrescriptionInput input) {
        if (!visitClient.checkVisitExists(UUID.fromString(input.getVisitId()), doctorId))
            throw new IllegalArgumentException("Visit does not exist");

        VisitDTO visitDTO = visitClient.getVisit(UUID.fromString(input.getVisitId()));

        if (visitDTO.visitStatus().equals(VisitStatus.CANCELLED))
            throw new IllegalArgumentException("Visit status is CANCELLED");

        if (visitDTO.visitTime().isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Visit has not happened yet");

        PatientRecordDTO recordDTO = recordClient.getPatientRecord(visitDTO.patientId().toString());
        List<String> medIds = input.getMedications().stream().map(PrescribedMedicationInput::getMedicationId).toList();

        medIds.forEach(medication -> {
            if (!medicationClient.isMedication(medication))
                throw new IllegalArgumentException("Medication does not exist");

            MedicationDetailsDTO details = medicationClient.getMedicationDetails(medication);

            for (String ingredient : details.ingredients()) {
                if (recordDTO.allergies().contains(ingredient) && !input.getIgnoreAllergies())
                    throw new IllegalArgumentException("Patient allergic to ingredient");
            }

            for (String interaction : details.interactions()) {
                if ((medIds.contains(interaction) || recordDTO.medications().contains(interaction)) && !input.getIgnoreInteractions())
                    throw new IllegalArgumentException("Medicines interact");
            }
        });

        Prescription prescription = Prescription.builder()
                .doctorId(doctorId)
                .patientId(visitDTO.patientId())
                .visitId(visitDTO.id())
                .medications(input.getMedications().stream().map(im -> PrescribedMedication.builder()
                        .medicationId(UUID.fromString(im.getMedicationId()))
                        .usageNotes(im.getUsageNotes())
                        .build()).collect(Collectors.toSet()))
                .build();

        return mapper.fromEntity(repository.save(prescription));
    }
}
