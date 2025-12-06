package edu.pk.jawolh.erecepta.patientrecordservice.service;

import com.example.demo.codegen.types.PatientInfo;
import com.example.demo.codegen.types.UpdatePatientInfoInput;
import edu.pk.jawolh.erecepta.patientrecordservice.client.GrpcUserClient;
import edu.pk.jawolh.erecepta.patientrecordservice.exception.*;
import edu.pk.jawolh.erecepta.patientrecordservice.mapper.BloodTypeMapper;
import edu.pk.jawolh.erecepta.patientrecordservice.mapper.PatientMapper;
import edu.pk.jawolh.erecepta.patientrecordservice.model.BloodType;
import edu.pk.jawolh.erecepta.patientrecordservice.model.PatientRecord;
import edu.pk.jawolh.erecepta.patientrecordservice.repository.PatientRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientRecordService {
    private final PatientRecordRepository patientRecordRepository;
    private final GrpcUserClient grpcUserClient;

    public PatientInfo getPatientInfo(UUID patientId) {
        PatientRecord patientRecord = getOrCreatePatientRecord(patientId);

        return PatientMapper.toDTO(patientRecord);
    }


    @Transactional
    public PatientInfo updatePatientInfo(UUID userId, UpdatePatientInfoInput input) {
        PatientRecord patient = getOrCreatePatientRecord(userId);

        Optional.ofNullable(BloodTypeMapper.fromDTO(input.getBloodType())).ifPresent(patient::setBloodType);
        Optional.ofNullable(input.getHeight()).ifPresent(patient::setHeight);
        Optional.ofNullable(input.getWeight()).ifPresent(patient::setWeight);
        Optional.ofNullable(input.getEmergencyContact()).ifPresent(patient::setEmergencyContact);

        log.info("Edited patient: " + patient.toString());

        PatientRecord saved = patientRecordRepository.save(patient);

        log.info("Saved patient: " + saved.toString());

        return PatientMapper.toDTO(saved);
    }

    @Transactional
    public PatientInfo addAllergy(UUID userId, String allergy) {
        PatientRecord patient = getOrCreatePatientRecord(userId);

        List<String> allergies = patient.getAllergies();

        if (allergies.contains(allergy))
            throw new AllergyAlreadyExistsException("Allergy already exists");

        allergies.add(allergy);
        patient.setAllergies(allergies);
        PatientRecord saved = patientRecordRepository.save(patient);
        log.info("Saved patient: " + saved);

        return PatientMapper.toDTO(saved);
    }

    @Transactional
    public PatientInfo removeAllergy(UUID userId, String allergy) {
        PatientRecord patient = getOrCreatePatientRecord(userId);

        List<String> allergies = patient.getAllergies();

        if (!allergies.contains(allergy))
            throw new AllergyNotFoundException("Allergy not found");

        allergies.remove(allergy);
        patient.setAllergies(allergies);
        PatientRecord saved = patientRecordRepository.save(patient);
        log.info("Saved patient: " + saved);

        return PatientMapper.toDTO(saved);
    }

    @Transactional
    public PatientInfo addMedication(UUID userId, UUID medicationId) {
        //todo communication with medication service to check if medication exists

        PatientRecord patient = getOrCreatePatientRecord(userId);

        List<UUID> medications = patient.getMedications();

        if (medications.contains(medicationId))
            throw new MedicationAlreadyExistsException("Medication already exists");

        medications.add(medicationId);
        patient.setMedications(medications);

        PatientRecord saved = patientRecordRepository.save(patient);

        return PatientMapper.toDTO(saved);
    }

    @Transactional
    public PatientInfo addChronicDisease(UUID userId, String disease) {
        PatientRecord patient = getOrCreatePatientRecord(userId);

        List<String> diseases = patient.getChronicDiseases();

        if (diseases.contains(disease))
            throw new DisaeseAlreadyExistsException("Disease already exists");

        diseases.add(disease);
        patient.setChronicDiseases(diseases);
        PatientRecord saved = patientRecordRepository.save(patient);
        log.info("Saved patient: " + saved);

        return PatientMapper.toDTO(saved);
    }

    @Transactional
    public PatientInfo removeChronicDisease(UUID userId, String disease) {
        PatientRecord patient = getOrCreatePatientRecord(userId);

        List<String> diseases = patient.getChronicDiseases();

        if (!diseases.contains(disease))
            throw new DisaeseNotFoundException("Disease not found");

        diseases.remove(disease);
        patient.setChronicDiseases(diseases);
        PatientRecord saved = patientRecordRepository.save(patient);
        log.info("Saved patient: " + saved);

        return PatientMapper.toDTO(saved);
    }


    private PatientRecord getOrCreatePatientRecord(UUID patientId) {
        ensureUserExists(patientId);

        return patientRecordRepository.findById(patientId)
                .orElseGet(() -> patientRecordRepository.save(PatientRecord.builder()
                        .userId(patientId)
                        .allergies(new ArrayList<>())
                        .medications(new ArrayList<>())
                        .chronicDiseases(new ArrayList<>())
                        .build()));
    }

    private void ensureUserExists(UUID userId) {
        if (!grpcUserClient.isPatient(userId.toString())) {
            throw new UserDoesNotExistException("User with given id does not exist");
        }

    }
}
