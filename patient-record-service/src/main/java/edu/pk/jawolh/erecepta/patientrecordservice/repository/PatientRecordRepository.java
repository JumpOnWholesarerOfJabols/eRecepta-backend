package edu.pk.jawolh.erecepta.patientrecordservice.repository;

import edu.pk.jawolh.erecepta.patientrecordservice.model.PatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientRecordRepository extends JpaRepository<PatientRecord, UUID> {
}
