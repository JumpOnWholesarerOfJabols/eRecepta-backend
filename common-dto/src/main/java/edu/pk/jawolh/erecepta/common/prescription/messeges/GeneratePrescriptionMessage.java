package edu.pk.jawolh.erecepta.common.prescription.messeges;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePrescriptionMessage {
    private String prescriptionId;
    private String accessCode;
    private Integer totalPackages;
    private String issueDate;
    private String expirationDate;
    private String status;
    private String patientName;
    private String patientPesel;
    private String doctorName;
    private String medicationName;
    private String patientEmail;
}