package edu.pk.jawolh.erecepta.med_docs_service.service;


import edu.pk.jawolh.erecepta.med_docs_service.exceptions.PrescriptionNotFoundException;
import edu.pk.jawolh.erecepta.med_docs_service.mappers.PrescriptionMapper;
import edu.pk.jawolh.erecepta.med_docs_service.model.Prescription;
import edu.pk.jawolh.erecepta.med_docs_service.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public com.example.demo.codegen.types.Prescription verifyPrescription(String accessCode, UUID patientIdentifier) {

        Prescription prescription = prescriptionRepository
                .findByPatientIdAndAccessCode(patientIdentifier, accessCode)
                .orElseThrow(()-> new PrescriptionNotFoundException("Prescription not found"));

        return PrescriptionMapper.toDTO(prescription);
    }


}
