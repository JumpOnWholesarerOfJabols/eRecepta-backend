package edu.pk.jawolh.erecepta.patientrecordservice.mapper;

import com.example.demo.codegen.types.PatientInfo;
import edu.pk.jawolh.erecepta.patientrecordservice.model.PatientRecord;

import java.util.UUID;

public class PatientMapper {
    public static PatientInfo toDTO(PatientRecord record){
        return PatientInfo.newBuilder()
                .userId(record.getUserId().toString())
                .bloodType(record.getBloodType())
                .height(record.getHeight())
                .weight(record.getWeight())
                .allergies(record.getAllergies())
                .chronicDiseases(record.getChronicDiseases())
                .medications(record.getMedications().stream()
                        .map(UUID::toString)
                        .toList())
                .emergencyContact(record.getEmergencyContact())
                .build();
    }
}
