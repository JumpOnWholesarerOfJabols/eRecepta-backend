package edu.pk.jawolh.erecepta.prescriptionservice.mapper;

import com.example.demo.codegen.types.PrescribedMedication;
import org.springframework.stereotype.Component;

@Component
public class PrescribedMedicationMapper {
    public PrescribedMedication fromEntity(edu.pk.jawolh.erecepta.prescriptionservice.model.PrescribedMedication md) {
        return PrescribedMedication.newBuilder()
                .medicationId(md.getMedicationId().toString())
                .usageNotes(md.getUsageNotes())
                .build();
    }
}
