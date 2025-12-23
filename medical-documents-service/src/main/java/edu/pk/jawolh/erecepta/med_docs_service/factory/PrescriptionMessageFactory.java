package edu.pk.jawolh.erecepta.med_docs_service.factory;

import edu.pk.jawolh.erecepta.common.medication.proto.GetMedicationDataReply;
import edu.pk.jawolh.erecepta.common.prescription.messeges.GeneratePrescriptionMessage;
import edu.pk.jawolh.erecepta.common.user.proto.GetUserDataReply;
import edu.pk.jawolh.erecepta.med_docs_service.client.GrpcMedicationClient;
import edu.pk.jawolh.erecepta.med_docs_service.client.GrpcUserClient;
import edu.pk.jawolh.erecepta.med_docs_service.model.Prescription;
import edu.pk.jawolh.erecepta.med_docs_service.repository.PrescriptionRepository;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrescriptionMessageFactory {
    private final GrpcUserClient grpcUserClient;
    private final GrpcMedicationClient grpcMedicationClient;


    public GeneratePrescriptionMessage createMessage(Prescription prescription) {
        log.info("Aggregating data for prescription PDF: {}", prescription.getId());

        GetUserDataReply patientData = grpcUserClient.getUserData(prescription.getPatientId().toString());

        GetUserDataReply doctorData = grpcUserClient.getUserData(prescription.getDoctorId().toString());

        GetMedicationDataReply medicationData = grpcMedicationClient.getMedicationData(prescription.getMedicationId().toString());
        String medicationName = medicationData.getTradeName();

        return GeneratePrescriptionMessage.builder()
                .prescriptionId(prescription.getId().toString())
                .accessCode(prescription.getAccessCode())
                .status(prescription.getStatus().name())
                .issueDate(prescription.getCreated().toString())
                .expirationDate(prescription.getExpiresAt().toString())
                .totalPackages(prescription.getTotalPackages())

                .patientName(patientData.getFirstName() + " " + patientData.getLastName())
                .patientPesel(patientData.getPesel())
                .patientEmail(patientData.getEmail())
                .doctorName(doctorData.getFirstName() + " " + doctorData.getLastName())
                .medicationName(medicationName)
                .build();
    }
}
