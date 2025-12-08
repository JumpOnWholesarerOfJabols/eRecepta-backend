package edu.pk.jawolh.erecepta.medicationservice.service;

import com.example.demo.codegen.types.*;
import edu.pk.jawolh.erecepta.medicationservice.exception.MedicationNotFoundException;
import edu.pk.jawolh.erecepta.medicationservice.mapper.DrugInteractionMapper;
import edu.pk.jawolh.erecepta.medicationservice.mapper.MedicationMapper;
import edu.pk.jawolh.erecepta.medicationservice.model.DrugInteraction;
import edu.pk.jawolh.erecepta.medicationservice.model.Medication;
import edu.pk.jawolh.erecepta.medicationservice.repository.DrugInteractionRepository;
import edu.pk.jawolh.erecepta.medicationservice.repository.MedicationDAO;
import edu.pk.jawolh.erecepta.medicationservice.repository.MedicationRepository;
import edu.pk.jawolh.erecepta.medicationservice.validator.MedicationInputValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final MedicationInputValidator medicationInputValidator;

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

    @Transactional
    public com.example.demo.codegen.types.Medication createMedication(CreateMedicationInput input) {
        log.debug("Attempting to create medication with EAN: {}", input.getEan());

        medicationInputValidator.validateCreationInput(input);

        if (medicationRepository.existsByEan(input.getEan())) {
            log.warn("Creation failed. Medication with EAN {} already exists.", input.getEan());
            throw new IllegalArgumentException("Medication with EAN " + input.getEan() + " already exists.");
        }

        Medication medication = MedicationMapper.fromCreateInputToDomain(input);

        Medication saved = medicationRepository.save(medication);

        return MedicationMapper.toDTO(saved);
    }

    @Transactional
    public com.example.demo.codegen.types.Medication patchMedication(UUID id, PatchMedicationInput input) {
        medicationInputValidator.validatePatchInput(input);

        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new MedicationNotFoundException("Medication with id " + id + " not found"));

        if (input.getEan() != null && !input.getEan().equals(medication.getEan())) {
            if (medicationRepository.existsByEan(input.getEan())) {
                throw new IllegalArgumentException("Medication with EAN " + input.getEan() + " already exists.");
            }
            medication.setEan(input.getEan());
        }

        if (input.getAtcCode() != null) {
            medication.setAtcCode(input.getAtcCode());
        }
        if (input.getTradeName() != null) {
            medication.setTradeName(input.getTradeName());
        }
        if (input.getGenericName() != null) {
            medication.setGenericName(input.getGenericName());
        }
        if (input.getManufacturer() != null) {
            medication.setManufacturer(input.getManufacturer());
        }
        if (input.getPackageSize() != null) {
            medication.setPackageSize(input.getPackageSize());
        }
        if (input.getRequiresPrescription() != null) {
            medication.setRequiresPrescription(input.getRequiresPrescription());
        }
        if (input.getForm() != null) {
            medication.setForm(edu.pk.jawolh.erecepta.medicationservice.mapper.MedicationFormMapper.fromDTO(input.getForm()));
        }
        if (input.getRoute() != null) {
            medication.setRoute(edu.pk.jawolh.erecepta.medicationservice.mapper.RouteOfAdministrationMapper.fromDTO(input.getRoute()));
        }

        Medication saved = medicationRepository.save(medication);

        log.info("Successfully patched medication with ID: {}", saved.getId());
        return MedicationMapper.toDTO(saved);


    }
}
