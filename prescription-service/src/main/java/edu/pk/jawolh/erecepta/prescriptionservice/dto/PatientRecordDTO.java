package edu.pk.jawolh.erecepta.prescriptionservice.dto;

import java.util.List;

public record PatientRecordDTO(List<String> allergies, List<String> medications) {
}
