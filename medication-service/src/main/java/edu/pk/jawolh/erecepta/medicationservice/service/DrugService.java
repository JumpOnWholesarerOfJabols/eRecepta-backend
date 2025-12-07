package edu.pk.jawolh.erecepta.medicationservice.service;

import com.example.demo.codegen.types.MedicationFilterInput;
import edu.pk.jawolh.erecepta.medicationservice.mapper.MedicationMapper;
import edu.pk.jawolh.erecepta.medicationservice.model.Medication;
import edu.pk.jawolh.erecepta.medicationservice.repository.MedicationDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrugService {

    private final MedicationDAO medicationDAO;

    public List<com.example.demo.codegen.types.Medication> getMedicationByFilter(
            MedicationFilterInput filter,
            Integer limit,
            Integer offset) {

        List<Medication> fromDb = medicationDAO.findByFilter(filter, limit, offset);

        return fromDb.stream().map(MedicationMapper::toDTO).toList();
    }
}
