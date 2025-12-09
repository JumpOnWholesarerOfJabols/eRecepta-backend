package edu.pk.jawolh.erecepta.medicationservice.service;

import com.example.demo.codegen.types.*;
import edu.pk.jawolh.erecepta.medicationservice.exception.IngredientNotFoundException;
import edu.pk.jawolh.erecepta.medicationservice.exception.MedicationNotFoundException;
import edu.pk.jawolh.erecepta.medicationservice.repository.DrugInteractionRepository;
import edu.pk.jawolh.erecepta.medicationservice.repository.MedicationDAO;
import edu.pk.jawolh.erecepta.medicationservice.repository.MedicationRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DrugServiceIntegrationTest {

    @Autowired
    private DrugService drugService;

    @Autowired
    private MedicationRepository medicationRepository;

    @MockitoBean
    private MedicationDAO medicationDAO;

    @MockitoBean
    private DrugInteractionRepository drugInteractionRepository;

    @AfterEach
    void tearDown() {
        medicationRepository.deleteAll();
    }

    private CreateMedicationInput generateValidMedicationInput() {
        return Instancio.of(CreateMedicationInput.class)
                .generate(field(CreateMedicationInput::getEan), gen -> gen.string().digits().length(13))
                .set(field(CreateMedicationInput::getAtcCode), "A01AA01")
                .set(field(IngredientInput::getStrength), "500 mg")
                .generate(field(CreateMedicationInput::getIngredients), gen -> gen.collection().minSize(1))
                .set(field(CreateMedicationInput::getPackageSize), "30 tablets")
                .create();
    }

    @Nested
    class CreateMedicationTests {

        @Test
        void shouldCreateMedication_whenInputIsValid() {
            CreateMedicationInput input = generateValidMedicationInput();

            Medication result = drugService.createMedication(input);

            assertNotNull(result.getId());
            assertEquals(input.getEan(), result.getEan());
            assertEquals(input.getTradeName(), result.getTradeName());
            assertEquals(input.getIngredients().size(), result.getIngredients().size());

            assertTrue(medicationRepository.existsByEan(input.getEan()));
        }

        @Test
        void shouldThrowException_whenEanAlreadyExists() {
            CreateMedicationInput input = generateValidMedicationInput();
            drugService.createMedication(input);

            CreateMedicationInput duplicateInput = generateValidMedicationInput();
            duplicateInput.setEan(input.getEan());

            assertThrows(IllegalArgumentException.class, () ->
                    drugService.createMedication(duplicateInput));
        }

        @Test
        void shouldThrowException_whenInputIsInvalid() {
            CreateMedicationInput input = generateValidMedicationInput();
            input.setEan("invalid-ean");

            assertThrows(IllegalArgumentException.class, () ->
                    drugService.createMedication(input));
        }
    }

    @Nested
    class PatchMedicationTests {

        private UUID medicationId;
        private Medication existingMedication;

        @BeforeEach
        void setUp() {
            CreateMedicationInput createInput = generateValidMedicationInput();
            existingMedication = drugService.createMedication(createInput);
            medicationId = UUID.fromString(existingMedication.getId());
        }

        @Test
        void shouldPatchFields_whenInputIsValid() {
            String newTradeName = "New Trade Name";
            String newEan = "1234567890123";

            PatchMedicationInput patchInput = new PatchMedicationInput();
            patchInput.setTradeName(newTradeName);
            patchInput.setEan(newEan);

            Medication result = drugService.patchMedication(medicationId, patchInput);

            assertEquals(newTradeName, result.getTradeName());
            assertEquals(newEan, result.getEan());
            assertEquals(existingMedication.getGenericName(), result.getGenericName());
        }

        @Test
        void shouldThrowException_whenPatchingWithExistingEan() {
            CreateMedicationInput otherInput = generateValidMedicationInput();
            Medication otherMedication = drugService.createMedication(otherInput);

            PatchMedicationInput patchInput = new PatchMedicationInput();
            patchInput.setEan(otherMedication.getEan());

            assertThrows(IllegalArgumentException.class, () ->
                    drugService.patchMedication(medicationId, patchInput));
        }

        @Test
        void shouldThrowException_whenMedicationNotFound() {
            UUID nonExistentId = UUID.randomUUID();
            PatchMedicationInput patchInput = new PatchMedicationInput();

            assertThrows(MedicationNotFoundException.class, () ->
                    drugService.patchMedication(nonExistentId, patchInput));
        }
    }

    @Nested
    class IngredientTests {

        private UUID medicationId;

        @BeforeEach
        void setUp() {
            CreateMedicationInput createInput = generateValidMedicationInput();
            Medication med = drugService.createMedication(createInput);
            medicationId = UUID.fromString(med.getId());
        }

        @Test
        void shouldAddIngredient_whenNew() {
            IngredientInput input = Instancio.of(IngredientInput.class)
                    .generate(field(IngredientInput::getStrength), gen -> gen.text().pattern("10 mg"))
                    .create();

            Medication result = drugService.addIngredient(medicationId, input);

            assertTrue(result.getIngredients().stream()
                    .anyMatch(ing -> ing.getName().equals(input.getName())));
        }

        @Test
        void shouldThrowException_whenAddingExistingIngredient() {
            IngredientInput input = Instancio.of(IngredientInput.class)
                    .generate(field(IngredientInput::getStrength), gen -> gen.text().pattern("10 mg"))
                    .create();

            drugService.addIngredient(medicationId, input);

            assertThrows(IllegalArgumentException.class, () ->
                    drugService.addIngredient(medicationId, input));
        }

        @Test
        void shouldUpdateIngredient_whenExists() {
            IngredientInput input = Instancio.of(IngredientInput.class)
                    .generate(field(IngredientInput::getStrength), gen -> gen.text().pattern("10 mg"))
                    .create();
            Medication medWithIngredient = drugService.addIngredient(medicationId, input);

            Ingredient addedIngredient = medWithIngredient.getIngredients().stream()
                    .filter(i -> i.getName().equals(input.getName()))
                    .findFirst()
                    .orElseThrow();
            UUID ingredientId = UUID.fromString(addedIngredient.getId());

            UpdateIngredientInput updateInput = new UpdateIngredientInput();
            updateInput.setStrength("200 mg");

            Medication result = drugService.updateIngredient(medicationId, ingredientId, updateInput);

            Ingredient updated = result.getIngredients().stream()
                    .filter(i -> i.getId().equals(ingredientId.toString()))
                    .findFirst().orElseThrow();

            assertEquals("200 mg", updated.getStrength());
            assertEquals(input.getName(), updated.getName());
        }

        @Test
        void shouldThrowException_whenUpdatingToExistingName() {
            IngredientInput ing1 = Instancio.of(IngredientInput.class)
                    .set(field(IngredientInput::getName), "Ingredient A")
                    .set(field(IngredientInput::getStrength), "10mg")
                    .create();
            IngredientInput ing2 = Instancio.of(IngredientInput.class)
                    .set(field(IngredientInput::getName), "Ingredient B")
                    .set(field(IngredientInput::getStrength), "10mg")
                    .create();

            drugService.addIngredient(medicationId, ing1);
            Medication med = drugService.addIngredient(medicationId, ing2);

            UUID ing2Id = UUID.fromString(med.getIngredients().stream()
                    .filter(i -> i.getName().equals("Ingredient B"))
                    .findFirst().orElseThrow().getId());

            UpdateIngredientInput updateInput = new UpdateIngredientInput();
            updateInput.setName("Ingredient A");

            assertThrows(IllegalArgumentException.class, () ->
                    drugService.updateIngredient(medicationId, ing2Id, updateInput));
        }

        @Test
        void shouldRemoveIngredient_whenExists() {
            IngredientInput input = Instancio.of(IngredientInput.class)
                    .generate(field(IngredientInput::getStrength), gen -> gen.text().pattern("10 mg"))
                    .create();
            Medication med = drugService.addIngredient(medicationId, input);
            UUID ingId = UUID.fromString(med.getIngredients().get(0).getId());

            assertFalse(med.getIngredients().isEmpty());

            Medication result = drugService.removeIngredient(medicationId, ingId);

            boolean stillExists = result.getIngredients().stream()
                    .anyMatch(i -> i.getId().equals(ingId.toString()));
            assertFalse(stillExists);
        }

        @Test
        void shouldThrowException_whenRemovingNonExistingIngredient() {
            UUID randomId = UUID.randomUUID();
            assertThrows(IngredientNotFoundException.class, () ->
                    drugService.removeIngredient(medicationId, randomId));
        }
    }

    @Nested
    class IndicationTests {

        private UUID medicationId;

        @BeforeEach
        void setUp() {
            CreateMedicationInput createInput = generateValidMedicationInput();
            createInput.setIndications(List.of());
            Medication med = drugService.createMedication(createInput);
            medicationId = UUID.fromString(med.getId());
        }

        @Test
        void shouldAddIndication_whenNew() {
            String indication = "Headache";
            Medication result = drugService.addIndication(medicationId, indication);

            assertTrue(result.getIndications().contains(indication));
        }

        @Test
        void shouldThrowException_whenAddingDuplicateIndication() {
            String indication = "Headache";
            drugService.addIndication(medicationId, indication);

            assertThrows(IllegalArgumentException.class, () ->
                    drugService.addIndication(medicationId, "headache"));
        }

        @Test
        void shouldRemoveIndication_whenExists() {
            String indication = "Fever";
            drugService.addIndication(medicationId, indication);

            Medication result = drugService.removeIndication(medicationId, "fever");

            assertFalse(result.getIndications().contains(indication));
        }
    }

    @Nested
    class SideEffectTests {

        private UUID medicationId;

        @BeforeEach
        void setUp() {
            CreateMedicationInput createInput = generateValidMedicationInput();
            createInput.setSideEffects(List.of());
            Medication med = drugService.createMedication(createInput);
            medicationId = UUID.fromString(med.getId());
        }

        @Test
        void shouldAddSideEffect_whenNew() {
            String sideEffect = "Nausea";
            Medication result = drugService.addSideEffect(medicationId, sideEffect);

            assertTrue(result.getSideEffects().contains(sideEffect));
        }

        @Test
        void shouldRemoveSideEffect_whenExists() {
            String sideEffect = "Dizziness";
            drugService.addSideEffect(medicationId, sideEffect);

            Medication result = drugService.removeSideEffect(medicationId, sideEffect);

            assertFalse(result.getSideEffects().contains(sideEffect));
        }
    }
}