package edu.pk.jawolh.erecepta.medicationservice.mapper;

import com.example.demo.codegen.types.Ingredient;

public class IngredientMapper {
    public static Ingredient toDTO(edu.pk.jawolh.erecepta.medicationservice.model.Ingredient domainIngredient) {
        if (domainIngredient == null) {
            return null;
        }

        return Ingredient.newBuilder()
                .name(domainIngredient.getName())
                .strength(domainIngredient.getStrength())
                .isActive(domainIngredient.isActive())
                .build();
    }
}