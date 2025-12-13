package edu.pk.jawolh.erecepta.medicationservice.mapper;

import com.example.demo.codegen.types.CreateMedicationInput;
import com.example.demo.codegen.types.Medication;
import edu.pk.jawolh.erecepta.medicationservice.model.Ingredient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MedicationMapper {
    public static Medication toDTO(edu.pk.jawolh.erecepta.medicationservice.model.Medication domainMedication) {
        if (domainMedication == null) {
            return null;
        }

        return Medication.newBuilder()
                .id(domainMedication.getId().toString())
                .ean(domainMedication.getEan())
                .atcCode(domainMedication.getAtcCode())
                .tradeName(domainMedication.getTradeName())
                .genericName(domainMedication.getGenericName())
                .manufacturer(domainMedication.getManufacturer())
                .form(MedicationFormMapper.toDTO(domainMedication.getForm()))
                .route(RouteOfAdministrationMapper.toDTO(domainMedication.getRoute()))
                .packageSize(domainMedication.getPackageSize())
                .requiresPrescription(domainMedication.isRequiresPrescription())
                .ingredients(domainMedication.getIngredients().stream()
                        .map(IngredientMapper::toDTO)
                        .collect(Collectors.toList()))
                .indications(domainMedication.getIndications())
                .sideEffects(domainMedication.getSideEffects())
                .build();
    }

    public static edu.pk.jawolh.erecepta.medicationservice.model.Medication fromCreateInputToDomain(CreateMedicationInput input) {
        if (input == null) {
            return null;
        }
        List<Ingredient> ingredients = input.getIngredients() != null
                ? input.getIngredients().stream()
                .map(IngredientMapper::fromInputToDomain)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return edu.pk.jawolh.erecepta.medicationservice.model.Medication.builder()
                .ean(input.getEan())
                .atcCode(input.getAtcCode())
                .tradeName(input.getTradeName())
                .genericName(input.getGenericName())
                .manufacturer(input.getManufacturer())
                .form(MedicationFormMapper.fromDTO(input.getForm()))
                .route(RouteOfAdministrationMapper.fromDTO(input.getRoute()))
                .packageSize(input.getPackageSize())
                .requiresPrescription(input.getRequiresPrescription())
                .ingredients(ingredients)
                .indications(input.getIndications() != null ? input.getIndications() : Collections.emptyList())
                .sideEffects(input.getSideEffects() != null ? input.getSideEffects() : Collections.emptyList())
                .build();
    }
}