package edu.pk.jawolh.erecepta.med_docs_service.mappers;

import com.example.demo.codegen.types.Prescription;

public class PrescriptionMapper {

    public static Prescription toDTO(edu.pk.jawolh.erecepta.med_docs_service.model.Prescription domainPrescription) {
        if (domainPrescription == null) {
            return null;
        }

        return Prescription.newBuilder()
                .id(domainPrescription.getId() != null ? domainPrescription.getId().toString() : null)
                .accessCode(domainPrescription.getAccessCode())
                .status(PrescriptionStatusMapper.toDTO(domainPrescription.getStatus()))
                .doctorId(domainPrescription.getDoctorId() != null ? domainPrescription.getDoctorId().toString() : null)
                .patientId(domainPrescription.getPatientId() != null ? domainPrescription.getPatientId().toString() : null)
                .medicationId(domainPrescription.getMedicationId() != null ? domainPrescription.getMedicationId().toString() : null)
                .totalPackages(domainPrescription.getTotalPackages())
                .filledPackages(domainPrescription.getFilledPackages())
                .remainingPackages(domainPrescription.getRemainingPackages())
                .createdAt(domainPrescription.getCreated() != null ? domainPrescription.getCreated().toString() : null)
                .expiresAt(domainPrescription.getExpiresAt() != null ? domainPrescription.getExpiresAt().toString() : null)
                .build();
    }
}
