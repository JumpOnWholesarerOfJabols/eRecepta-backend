package edu.pk.jawolh.erecepta.doc_gen.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrescriptionPdfData {
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
}
