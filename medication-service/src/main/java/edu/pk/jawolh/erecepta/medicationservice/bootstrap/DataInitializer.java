package edu.pk.jawolh.erecepta.medicationservice.bootstrap;

import edu.pk.jawolh.erecepta.medicationservice.model.*;
import edu.pk.jawolh.erecepta.medicationservice.repository.DrugInteractionRepository;
import edu.pk.jawolh.erecepta.medicationservice.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MedicationRepository medicationRepository;
    private final DrugInteractionRepository drugInteractionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (medicationRepository.count() == 0) {
            loadData();
        }
    }

    private void loadData() {
        List<Medication> meds = new ArrayList<>();

        meds.add(createMed("Apap", "Paracetamolum", "Paracetamol", "500 mg", "N02BE01", "59099901", MedicationForm.TABLET, List.of("Headache", "Fever")));
        meds.add(createMed("Ibuprom", "Ibuprofenum", "Ibuprofen", "200 mg", "M01AE01", "59099902", MedicationForm.TABLET, List.of("Pain", "Inflammation")));
        meds.add(createMed("Ketonal", "Ketoprofenum", "Ketoprofen", "100 mg", "M01AE03", "59099903", MedicationForm.CAPSULE, List.of("Strong pain", "Rheumatism")));
        meds.add(createMed("Aspirin", "Acidum acetylsalicylicum", "Acetylsalicylic acid", "500 mg", "N02BA01", "59099904", MedicationForm.TABLET, List.of("Cold", "Pain")));
        meds.add(createMed("Pyralgina", "Metamizolum natricum", "Metamizole sodium", "500 mg", "N02BB02", "59099905", MedicationForm.SOLUTION_FOR_INJECTION, List.of("Severe pain", "High fever")));

        meds.add(createMed("Duomox", "Amoxicillinum", "Amoxicillin", "1000 mg", "J01CA04", "59099906", MedicationForm.TABLET, List.of("Bacterial infections", "Tonsillitis")));
        meds.add(createMed("Augmentin", "Amoxicillinum + Acidum clavulanicum", "Amoxicillin", "875 mg", "J01CR02", "59099907", MedicationForm.TABLET, List.of("Pneumonia", "Ear infection")));
        meds.add(createMed("Azimycin", "Azithromycinum", "Azithromycin", "500 mg", "J01FA10", "59099908", MedicationForm.TABLET, List.of("Respiratory tract infections")));

        meds.add(createMed("Polpril", "Ramiprilum", "Ramipril", "5 mg", "C09AA05", "59099909", MedicationForm.TABLET, List.of("Hypertension", "Heart failure")));
        meds.add(createMed("Bisocard", "Bisoprololi fumaras", "Bisoprolol", "5 mg", "C07AB07", "59099910", MedicationForm.TABLET, List.of("Coronary artery disease", "Hypertension")));
        meds.add(createMed("Furosemidum", "Furosemidum", "Furosemide", "40 mg", "C03CA01", "59099911", MedicationForm.TABLET, List.of("Edema", "Hypertension")));

        meds.add(createMed("No-Spa", "Drotaverinum", "Drotaverine", "40 mg", "A03AD02", "59099912", MedicationForm.TABLET, List.of("Smooth muscle spasms", "Abdominal pain")));
        meds.add(createMed("Controloc", "Pantoprazolum", "Pantoprazole", "20 mg", "A02BC02", "59099913", MedicationForm.TABLET, List.of("Heartburn", "Reflux")));
        meds.add(createMed("Espumisan", "Simeticonum", "Simethicone", "40 mg", "A03AX13", "59099914", MedicationForm.CAPSULE, List.of("Bloating", "Flatulence")));

        meds.add(createMed("Zyrtec", "Cetirizinum", "Cetirizine", "10 mg", "R06AE07", "59099915", MedicationForm.TABLET, List.of("Allergy", "Hay fever")));
        meds.add(createMed("Claritine", "Loratadinum", "Loratadine", "10 mg", "R06AX13", "59099916", MedicationForm.TABLET, List.of("Allergy", "Hives")));

        meds.add(createMed("Metformax", "Metformini hydrochloridum", "Metformin", "500 mg", "A10BA02", "59099917", MedicationForm.TABLET, List.of("Type 2 diabetes")));
        meds.add(createMed("Magne B6", "Magnesium + Pyridoxinum", "Magnesium", "48 mg", "A11EC", "59099918", MedicationForm.TABLET, List.of("Magnesium deficiency", "Stress")));
        meds.add(createMed("Rutinoscorbin", "Rutosidum + Acidum ascorbicum", "Rutoside", "25 mg", "C05CA51", "59099919", MedicationForm.TABLET, List.of("Capillary fragility", "Vitamin C deficiency")));
        meds.add(createMed("Otrivin", "Xylometazolinum", "Xylometazoline", "0.1%", "R01AA07", "59099920", MedicationForm.INHALER, List.of("Runny nose", "Nasal congestion")));

        medicationRepository.saveAll(meds);

        createInteractions(meds);
    }

    private Medication createMed(String tradeName, String genericName, String ingredientName, String strength,
                                 String atc, String ean, MedicationForm form, List<String> indications) {
        Ingredient ingredient = Ingredient.builder()
                .name(ingredientName)
                .strength(strength)
                .isActive(true)
                .build();

        return Medication.builder()
                .ean(ean)
                .atcCode(atc)
                .tradeName(tradeName)
                .genericName(genericName)
                .manufacturer("Test Pharma S.A.")
                .form(form)
                .route(RouteOfAdministration.ORAL)
                .packageSize("Standard package")
                .requiresPrescription(true)
                .ingredients(List.of(ingredient))
                .indications(indications)
                .sideEffects(List.of("Nausea", "Headache"))
                .build();
    }

    private void createInteractions(List<Medication> meds) {
        Medication ibuprom = findByName(meds, "Ibuprom");
        Medication aspirin = findByName(meds, "Aspirin");
        Medication ketonal = findByName(meds, "Ketonal");
        Medication polpril = findByName(meds, "Polpril");
        Medication furosemidum = findByName(meds, "Furosemidum");
        Medication duomox = findByName(meds, "Duomox");
        Medication augmentin = findByName(meds, "Augmentin");

        saveInteraction(ibuprom, aspirin, RiskLevel.HIGH, "Increased risk of bleeding.");
        saveInteraction(ibuprom, ketonal, RiskLevel.CONTRAINDICATED, "Risk of kidney damage.");
        saveInteraction(polpril, furosemidum, RiskLevel.MODERATE, "Risk of hypotension.");
        saveInteraction(duomox, augmentin, RiskLevel.HIGH, "Risk of overdose.");
    }

    private Medication findByName(List<Medication> meds, String name) {
        return meds.stream()
                .filter(m -> m.getTradeName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void saveInteraction(Medication a, Medication b, RiskLevel level, String effect) {
        if (a != null && b != null) {
            DrugInteraction interaction = DrugInteraction.builder()
                    .id(new DrugInteractionId(a, b))
                    .riskLevel(level)
                    .effect(effect)
                    .build();
            drugInteractionRepository.save(interaction);
        }
    }
}