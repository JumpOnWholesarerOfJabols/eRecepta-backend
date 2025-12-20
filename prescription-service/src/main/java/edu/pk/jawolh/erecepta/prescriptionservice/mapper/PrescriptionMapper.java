package edu.pk.jawolh.erecepta.prescriptionservice.mapper;

import com.example.demo.codegen.types.Prescription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrescriptionMapper {
    private final PrescribedMedicationMapper mdMapper;

    public Prescription fromEntity(edu.pk.jawolh.erecepta.prescriptionservice.model.Prescription pr) {
        return Prescription.newBuilder()
                .id(pr.getId().toString())
                .medications(pr.getMedications().stream().map(mdMapper::fromEntity).toList())
                .visitId(pr.getVisitId().toString())
                .doctorId(pr.getDoctorId().toString())
                .patientId(pr.getPatientId().toString())
                .build();

    }
}
