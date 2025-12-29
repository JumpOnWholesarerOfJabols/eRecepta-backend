package edu.pk.jawolh.erecepta.common.prescription.messeges;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionEmailMessage {
    private String recipientEmail;
    private String patientName;
    private String prescriptionId;
    private String fileKey;
}
