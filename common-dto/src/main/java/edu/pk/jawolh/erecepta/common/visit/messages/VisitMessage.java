package edu.pk.jawolh.erecepta.common.visit.messages;

import edu.pk.jawolh.erecepta.common.visit.dtos.UserDataDTO;
import edu.pk.jawolh.erecepta.common.visit.dtos.VisitDataDTO;

public record VisitMessage(UserDataDTO patientData, UserDataDTO doctorData, VisitDataDTO visitData) {
}
