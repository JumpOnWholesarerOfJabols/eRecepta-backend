package edu.pk.jawolh.erecepta.medicationservice.service;

import com.example.demo.codegen.types.MedicationFilterInput;
import edu.pk.jawolh.erecepta.medicationservice.exception.MedicationNotFoundException;
import edu.pk.jawolh.erecepta.medicationservice.mapper.DrugInteractionMapper;
import edu.pk.jawolh.erecepta.medicationservice.mapper.MedicationMapper;
import edu.pk.jawolh.erecepta.medicationservice.model.DrugInteraction;
import edu.pk.jawolh.erecepta.medicationservice.model.Medication;
import edu.pk.jawolh.erecepta.medicationservice.repository.DrugInteractionRepository;
import edu.pk.jawolh.erecepta.medicationservice.repository.MedicationDAO;
import edu.pk.jawolh.erecepta.medicationservice.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrugService {

    private final MedicationDAO medicationDAO;
    private final MedicationRepository medicationRepository;
    private final DrugInteractionRepository drugInteractionRepository;

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

    public List<com.example.demo.codegen.types.DrugInteraction> checkInteractions(UUID targetMedicationId, List<UUID> currentMedicationIds) {

        log.debug("Checking interactions for medication with id {}", targetMedicationId);
        log.debug("Checking interactions for medication with id {}", currentMedicationIds);

        Iterable<DrugInteraction> medicineInteractions = drugInteractionRepository.findInteractionsForMedicationId(targetMedicationId);

        log.debug("all medicine interactions: " + medicineInteractions.toString());

        List<DrugInteraction> conflictingInteractions = StreamSupport
                .stream(medicineInteractions.spliterator(), false)
                        .filter(drugInteraction -> {
                            UUID medicationA = drugInteraction.getId().getMedicationA().getId();
                            UUID medicationB = drugInteraction.getId().getMedicationB().getId();

                            log.debug("medicationA: " + medicationA + ", medicationB: " + medicationB);

                            UUID interactionPartnerId = targetMedicationId.equals(medicationA) ?
                                            medicationB : medicationA;

                            log.debug("interactionPartnerId: " + interactionPartnerId);

                            return currentMedicationIds.contains(interactionPartnerId);
                        }).toList();

        log.debug("conflicting medicine interactions: " + conflictingInteractions.toString());

        return conflictingInteractions.stream().map(
                interaction-> DrugInteractionMapper.toDTO(interaction, targetMedicationId)).toList();
    }
}
