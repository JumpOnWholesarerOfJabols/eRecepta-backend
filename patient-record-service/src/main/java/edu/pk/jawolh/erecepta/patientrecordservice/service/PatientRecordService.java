package edu.pk.jawolh.erecepta.patientrecordservice.service;

import com.example.demo.codegen.types.PatientInfo;
import edu.pk.jawolh.erecepta.patientrecordservice.client.GrpcUserClient;
import edu.pk.jawolh.erecepta.patientrecordservice.exception.UserDoesNotExistException;
import edu.pk.jawolh.erecepta.patientrecordservice.mapper.PatientMapper;
import edu.pk.jawolh.erecepta.patientrecordservice.model.PatientRecord;
import edu.pk.jawolh.erecepta.patientrecordservice.repository.PatientRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientRecordService {
    private final PatientRecordRepository patientRecordRepository;
    private final GrpcUserClient grpcUserClient;

    public PatientInfo getPatientInfo(UUID patientId) {
        PatientRecord patientRecord = patientRecordRepository.findById(patientId)
                .orElseGet(() -> {
                    if (grpcUserClient.isPatient(patientId.toString())) {
                        return patientRecordRepository.save(
                                PatientRecord.builder()
                                        .userId(patientId)
                                        .allergies(new ArrayList<>())
                                        .medications(new ArrayList<>())
                                        .build()
                        );
                    } else {
                        throw new UserDoesNotExistException("User with given id does not exist");
                    }
                });

        return PatientMapper.toDTO(patientRecord);
    }


}
