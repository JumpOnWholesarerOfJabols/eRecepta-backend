package edu.pk.jawolh.erecepta.prescriptionservice.controller;

import com.example.demo.codegen.types.PrescribedMedication;
import com.example.demo.codegen.types.Prescription;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PrescriptionMapper {

    public Prescription toGraphQLType(edu.pk.jawolh.erecepta.prescriptionservice.model.Prescription entity) {
        return Prescription.newBuilder()
                .id(entity.getId().toString())
                .code(entity.getCode())
                .patientId(entity.getPatientId().toString())
                .doctorId(entity.getDoctorId().toString())
                .visitId(entity.getVisitId().toString())
                .medications(entity.getMedications().stream()
                        .map(this::toGraphQLMedication)
                        .collect(Collectors.toList()))
                .createdAt(entity.getCreatedAt().toString())
                .build();
    }

    private PrescribedMedication toGraphQLMedication(edu.pk.jawolh.erecepta.prescriptionservice.model.PrescribedMedication entity) {
        return PrescribedMedication.newBuilder()
                .id(entity.getId().toString())
                .medicationId(entity.getMedicationId().toString())
                .usageNotes(entity.getUsageNotes())
                .build();
    }
}
