package edu.pk.jawolh.erecepta.medicationservice.repository;

import edu.pk.jawolh.erecepta.medicationservice.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> { }
