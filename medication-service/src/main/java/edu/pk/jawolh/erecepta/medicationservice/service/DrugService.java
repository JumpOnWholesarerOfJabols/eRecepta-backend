package edu.pk.jawolh.erecepta.medicationservice.service;

import com.example.demo.codegen.types.MedicationFilterInput;
import edu.pk.jawolh.erecepta.medicationservice.exception.MedicationNotFoundException;
import edu.pk.jawolh.erecepta.medicationservice.mapper.MedicationMapper;
import edu.pk.jawolh.erecepta.medicationservice.model.Medication;
import edu.pk.jawolh.erecepta.medicationservice.repository.MedicationDAO;
import edu.pk.jawolh.erecepta.medicationservice.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrugService {

    private final MedicationDAO medicationDAO;
    private final MedicationRepository medicationRepository;

    public List<com.example.demo.codegen.types.Medication> getMedicationByFilter(
            MedicationFilterInput filter,
            Integer limit,
            Integer offset) {

        List<Medication> fromDb = medicationDAO.findByFilter(filter, limit, offset);

        return fromDb.stream().map(MedicationMapper::toDTO).toList();
    }

    public com.example.demo.codegen.types.Medication getMedicationById(UUID id) {

        Medication medication = medicationRepository.findById(id)
                .orElseThrow(()->new MedicationNotFoundException("Medication with current id does not exists"));

        return MedicationMapper.toDTO(medication);
    }
}
