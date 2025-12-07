package edu.pk.jawolh.erecepta.medicationservice.mapper;

import com.example.demo.codegen.types.Medication;

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
}