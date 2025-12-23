package edu.pk.jawolh.erecepta.medicationservice.mapper;

import com.example.demo.codegen.types.Ingredient;
import com.example.demo.codegen.types.IngredientInput;
import edu.pk.jawolh.erecepta.common.medication.proto.MedicationIngredient;

public class IngredientMapper {
    public static Ingredient toDTO(edu.pk.jawolh.erecepta.medicationservice.model.Ingredient domainIngredient) {
        if (domainIngredient == null) {
            return null;
        }

        return Ingredient.newBuilder()
                .id(domainIngredient.getId().toString())
                .name(domainIngredient.getName())
                .strength(domainIngredient.getStrength())
                .isActive(domainIngredient.isActive())
                .build();
    }

    public static edu.pk.jawolh.erecepta.medicationservice.model.Ingredient fromInputToDomain(IngredientInput input) {
        if (input == null) {
            return null;
        }

        return edu.pk.jawolh.erecepta.medicationservice.model.Ingredient.builder()
                .name(input.getName())
                .strength(input.getStrength())
                .isActive(input.getIsActive())
                .build();
    }

    public static MedicationIngredient toProto(edu.pk.jawolh.erecepta.medicationservice.model.Ingredient domainIngredient) {
        if (domainIngredient == null) {
            return null;
        }

        return MedicationIngredient.newBuilder()
                .setId(domainIngredient.getId() != null ? domainIngredient.getId().toString() : "")
                .setName(domainIngredient.getName())
                .setStrength(domainIngredient.getStrength())
                .setIsActive(domainIngredient.isActive())
                .build();
    }
}