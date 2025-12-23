package edu.pk.jawolh.erecepta.medicationservice.mapper;

import edu.pk.jawolh.erecepta.common.medication.proto.ProtoMedicationForm;
import edu.pk.jawolh.erecepta.medicationservice.model.MedicationForm;

public class MedicationFormMapper {

    public static com.example.demo.codegen.types.MedicationForm toDTO(MedicationForm domainType) {
        if (domainType == null) {
            return null;
        }

        return switch (domainType) {
            case TABLET -> com.example.demo.codegen.types.MedicationForm.TABLET;
            case CAPSULE -> com.example.demo.codegen.types.MedicationForm.CAPSULE;
            case SYRUP -> com.example.demo.codegen.types.MedicationForm.SYRUP;
            case SOLUTION_FOR_INJECTION -> com.example.demo.codegen.types.MedicationForm.SOLUTION_FOR_INJECTION;
            case OINTMENT -> com.example.demo.codegen.types.MedicationForm.OINTMENT;
            case GEL -> com.example.demo.codegen.types.MedicationForm.GEL;
            case INHALER -> com.example.demo.codegen.types.MedicationForm.INHALER;
            case PATCH -> com.example.demo.codegen.types.MedicationForm.PATCH;
        };
    }

    public static MedicationForm fromDTO(com.example.demo.codegen.types.MedicationForm dtoType) {
        if (dtoType == null) {
            return null;
        }

        return switch (dtoType) {
            case TABLET -> MedicationForm.TABLET;
            case CAPSULE -> MedicationForm.CAPSULE;
            case SYRUP -> MedicationForm.SYRUP;
            case SOLUTION_FOR_INJECTION -> MedicationForm.SOLUTION_FOR_INJECTION;
            case OINTMENT -> MedicationForm.OINTMENT;
            case GEL -> MedicationForm.GEL;
            case INHALER -> MedicationForm.INHALER;
            case PATCH -> MedicationForm.PATCH;
        };
    }

    public static ProtoMedicationForm toProto(MedicationForm domainType) {
        if (domainType == null) {
            return ProtoMedicationForm.FORM_UNSPECIFIED;
        }

        return switch (domainType) {
            case TABLET -> ProtoMedicationForm.FORM_TABLET;
            case CAPSULE -> ProtoMedicationForm.FORM_CAPSULE;
            case SYRUP -> ProtoMedicationForm.FORM_SYRUP;
            case SOLUTION_FOR_INJECTION -> ProtoMedicationForm.FORM_SOLUTION_FOR_INJECTION;
            case OINTMENT -> ProtoMedicationForm.FORM_OINTMENT;
            case GEL -> ProtoMedicationForm.FORM_GEL;
            case INHALER -> ProtoMedicationForm.FORM_INHALER;
            case PATCH -> ProtoMedicationForm.FORM_PATCH;
        };
    }
}