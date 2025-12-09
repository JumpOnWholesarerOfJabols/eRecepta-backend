package edu.pk.jawolh.erecepta.patientrecordservice;

import com.example.demo.codegen.types.*;
import edu.pk.jawolh.erecepta.patientrecordservice.client.GrpcMedicationClient;
import edu.pk.jawolh.erecepta.patientrecordservice.client.GrpcUserClient;
import edu.pk.jawolh.erecepta.patientrecordservice.exception.*;
import edu.pk.jawolh.erecepta.patientrecordservice.repository.PatientRecordRepository;
import edu.pk.jawolh.erecepta.patientrecordservice.service.PatientRecordService;
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

import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PatientRecordServiceIntegrationTest {

    @Autowired
    private PatientRecordService patientRecordService;

    @Autowired
    private PatientRecordRepository patientRecordRepository;

    @MockitoBean
    private GrpcUserClient grpcUserClient;

    @MockitoBean
    GrpcMedicationClient grpcMedicationClient;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = Instancio.create(UUID.class);
        when(grpcUserClient.isPatient(anyString())).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        patientRecordRepository.deleteAll();
    }

    @Nested
    class GetPatientInfoTests {

        @Test
        void shouldCreateEmptyRecord_whenUserExistsButRecordDoesNot() {
            PatientInfo result = patientRecordService.getPatientInfo(userId);

            assertNotNull(result);
            assertEquals(userId.toString(), result.getUserId());
            assertTrue(result.getAllergies().isEmpty());
            assertTrue(result.getChronicDiseases().isEmpty());
            assertTrue(result.getMedications().isEmpty());

            assertTrue(patientRecordRepository.findById(userId).isPresent());
        }

        @Test
        void shouldThrowUserDoesNotExistException_whenGrpcClientReturnsFalse() {
            UUID nonExistentId = Instancio.create(UUID.class);
            when(grpcUserClient.isPatient(nonExistentId.toString())).thenReturn(false);

            assertThrows(UserDoesNotExistException.class, () ->
                    patientRecordService.getPatientInfo(nonExistentId));
        }
    }

    @Nested
    class UpdatePatientInfoTests {

        @Test
        void shouldUpdateFields_whenInputIsValid() {
            patientRecordService.getPatientInfo(userId);

            UpdatePatientInfoInput input = Instancio.of(UpdatePatientInfoInput.class)
                    .generate(field(UpdatePatientInfoInput::getHeight), gen -> gen.doubles().range(50.0, 220.0))
                    .generate(field(UpdatePatientInfoInput::getWeight), gen -> gen.doubles().range(3.0, 150.0))
                    .generate(field(UpdatePatientInfoInput::getEmergencyContact), gen -> gen.text().pattern("#d#d#d#d#d#d#d#d#d"))
                    .create();

            PatientInfo result = patientRecordService.updatePatientInfo(userId, input);

            assertEquals(input.getBloodType(), result.getBloodType());
            assertEquals(input.getHeight(), result.getHeight());
            assertEquals(input.getWeight(), result.getWeight());
            assertEquals(input.getEmergencyContact(), result.getEmergencyContact());
        }

        @Test
        void shouldValidateInput_whenDataIsInvalid() {
            UpdatePatientInfoInput input = Instancio.of(UpdatePatientInfoInput.class)
                    .set(field(UpdatePatientInfoInput::getWeight), -10.0)
                    .create();

            assertThrows(RuntimeException.class, () ->
                    patientRecordService.updatePatientInfo(userId, input));
        }
    }

    @Nested
    class AllergyTests {

        @Test
        void shouldAddAllergy_whenNew() {
            patientRecordService.getPatientInfo(userId);

            String allergy = Instancio.of(String.class)
                    .generate(all(String.class), gen -> gen.string().alphaNumeric().length(5, 15))
                    .create();

            PatientInfo result = patientRecordService.addAllergy(userId, allergy);

            assertTrue(result.getAllergies().contains(allergy));
            assertEquals(1, result.getAllergies().size());
        }

        @Test
        void shouldThrowException_whenAddingExistingAllergy() {
            String allergy = Instancio.of(String.class)
                    .generate(all(String.class), gen -> gen.string().alphaNumeric())
                    .create();

            patientRecordService.addAllergy(userId, allergy);

            assertThrows(AllergyAlreadyExistsException.class, () ->
                    patientRecordService.addAllergy(userId, allergy));
        }

        @Test
        void shouldRemoveAllergy_whenExists() {
            String allergy = Instancio.of(String.class)
                    .generate(all(String.class), gen -> gen.string().alphaNumeric())
                    .create();

            patientRecordService.addAllergy(userId, allergy);

            PatientInfo result = patientRecordService.removeAllergy(userId, allergy);

            assertFalse(result.getAllergies().contains(allergy));
            assertTrue(result.getAllergies().isEmpty());
        }

        @Test
        void shouldThrowException_whenRemovingNonExistingAllergy() {
            patientRecordService.getPatientInfo(userId);

            String randomAllergy = Instancio.create(String.class);

            assertThrows(AllergyNotFoundException.class, () ->
                    patientRecordService.removeAllergy(userId, randomAllergy));
        }
    }

    @Nested
    class ChronicDiseaseTests {

        @Test
        void shouldAddDisease_whenNew() {
            patientRecordService.getPatientInfo(userId);

            String disease = Instancio.of(String.class)
                    .generate(all(String.class), gen -> gen.string().alphaNumeric().length(5, 20))
                    .create();

            PatientInfo result = patientRecordService.addChronicDisease(userId, disease);

            assertTrue(result.getChronicDiseases().contains(disease));
        }

        @Test
        void shouldThrowException_whenAddingExistingDisease() {
            String disease = Instancio.of(String.class)
                    .generate(all(String.class), gen -> gen.string().alphaNumeric())
                    .create();

            patientRecordService.addChronicDisease(userId, disease);

            assertThrows(DisaeseAlreadyExistsException.class, () ->
                    patientRecordService.addChronicDisease(userId, disease));
        }

        @Test
        void shouldRemoveDisease_whenExists() {
            String disease = Instancio.of(String.class)
                    .generate(all(String.class), gen -> gen.string().alphaNumeric())
                    .create();

            patientRecordService.addChronicDisease(userId, disease);

            PatientInfo result = patientRecordService.removeChronicDisease(userId, disease);

            assertFalse(result.getChronicDiseases().contains(disease));
        }

        @Test
        void shouldThrowException_whenRemovingNonExistingDisease() {
            patientRecordService.getPatientInfo(userId);

            String randomDisease = Instancio.create(String.class);

            assertThrows(DisaeseNotFoundException.class, () ->
                    patientRecordService.removeChronicDisease(userId, randomDisease));
        }
    }

    @Nested
    class MedicationTests {

        @Test
        void shouldAddMedication_whenNew() {
            patientRecordService.getPatientInfo(userId);
            UUID medId = Instancio.create(UUID.class);
            when(grpcMedicationClient.isMedication(anyString())).thenReturn(true);

            PatientInfo result = patientRecordService.addMedication(userId, medId);

            assertTrue(result.getMedications().contains(medId.toString()));
        }

        @Test
        void shouldThrowException_whenMedicationExists() {
            UUID medId = Instancio.create(UUID.class);
            when(grpcMedicationClient.isMedication(anyString())).thenReturn(true);

            patientRecordService.addMedication(userId, medId);

            assertThrows(MedicationAlreadyExistsException.class, () ->
                    patientRecordService.addMedication(userId, medId));
        }
    }

    @Nested
    class PatientHistoryTests {

        @Test
        void shouldRecordHistory_whenChangesAreMade() {
            patientRecordService.getPatientInfo(userId);

            UpdatePatientInfoInput input = Instancio.of(UpdatePatientInfoInput.class)
                    .generate(field(UpdatePatientInfoInput::getHeight), gen -> gen.doubles().range(1.0, 250.0))
                    .generate(field(UpdatePatientInfoInput::getWeight), gen -> gen.doubles().range(1.0, 200.0))
                    .generate(field(UpdatePatientInfoInput::getEmergencyContact), gen -> gen.text().pattern("#d#d#d#d#d#d#d#d#d"))
                    .create();

            patientRecordService.updatePatientInfo(userId, input);

            String allergy = Instancio.of(String.class)
                    .generate(all(String.class), gen -> gen.string().alphaNumeric())
                    .create();

            patientRecordService.addAllergy(userId, allergy);

            List<PatientHistoryEntry> history = patientRecordService.getPatientHistory(userId);

            assertNotNull(history);
            assertTrue(history.size() >= 3);

            boolean hasAdd = history.stream()
                    .anyMatch(h -> h.getRevisionType() == RevisionType.ADD);
            boolean hasMod = history.stream()
                    .anyMatch(h -> h.getRevisionType() == RevisionType.MOD);

            assertTrue(hasAdd);
            assertTrue(hasMod);

            boolean containsAllergyState = history.stream()
                    .anyMatch(h -> h.getPatientState().getAllergies().contains(allergy));
            assertTrue(containsAllergyState);
        }
    }
}