package edu.pk.jawolh.erecepta.patientrecordservice.service;

import com.example.demo.codegen.types.PatientInfo;
import com.example.demo.codegen.types.UpdatePatientInfoInput;
import edu.pk.jawolh.erecepta.patientrecordservice.client.GrpcUserClient;
import edu.pk.jawolh.erecepta.patientrecordservice.exception.UserDoesNotExistException;
import edu.pk.jawolh.erecepta.patientrecordservice.mapper.PatientMapper;
import edu.pk.jawolh.erecepta.patientrecordservice.model.PatientRecord;
import edu.pk.jawolh.erecepta.patientrecordservice.repository.PatientRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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


    public PatientInfo updatePatientInfo(UUID userId, UpdatePatientInfoInput input) {
        PatientRecord patient = getOrCreatePatientRecord(userId);

        Optional.ofNullable(input.getBloodType()).ifPresent(patient::setBloodType);
        Optional.ofNullable(input.getHeight()).ifPresent(patient::setHeight);
        Optional.ofNullable(input.getWeight()).ifPresent(patient::setWeight);
        Optional.ofNullable(input.getEmergencyContact()).ifPresent(patient::setEmergencyContact);

        log.debug("Edited patient: " + patient.toString());

        PatientRecord saved = patientRecordRepository.save(patient);

        log.info("Saved patient: " + saved.toString());

        return PatientMapper.toDTO(saved);
    }

    public PatientInfo addAllergy(UUID userId, String allergy) {
        return PatientMapper.toDTO(patientRecordRepository.save(getOrCreatePatientRecord(userId)));
    }

    public PatientInfo removeAllergy(UUID userId, String allergy) {
        return PatientMapper.toDTO(patientRecordRepository.save(getOrCreatePatientRecord(userId)));
    }

    public PatientInfo addMedication(UUID userId, String medication) {
        return PatientMapper.toDTO(patientRecordRepository.save(getOrCreatePatientRecord(userId)));
    }

    public PatientInfo removeMedication(UUID userId, String medication) {
        return PatientMapper.toDTO(patientRecordRepository.save(getOrCreatePatientRecord(userId)));
    }

    public PatientInfo addChronicDisease(UUID userId, String disease) {
        return PatientMapper.toDTO(patientRecordRepository.save(getOrCreatePatientRecord(userId)));
    }



    private PatientRecord getOrCreatePatientRecord(UUID patientId) {
        ensureUserExists(patientId);

        return patientRecordRepository.findById(patientId)
                .orElseGet(() -> patientRecordRepository.save(PatientRecord.builder()
                        .userId(patientId)
                        .allergies(new ArrayList<>())
                        .medications(new ArrayList<>())
                        .build()));
    }

    private void ensureUserExists(UUID userId) {
        if (!grpcUserClient.isPatient(userId.toString())) {
            throw new UserDoesNotExistException("User with given id does not exist");
        }

    }


}
