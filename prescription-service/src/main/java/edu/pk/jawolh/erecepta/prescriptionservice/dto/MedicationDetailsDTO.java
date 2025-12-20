package edu.pk.jawolh.erecepta.prescriptionservice.dto;

import java.util.List;

public record MedicationDetailsDTO(List<String> ingredients, List<String> interactions) {
}
