package edu.pk.jawolh.erecepta.patientrecordservice.mapper;

import edu.pk.jawolh.erecepta.patientrecordservice.model.BloodType;

public class BloodTypeMapper {

    public static com.example.demo.codegen.types.BloodType toDTO(BloodType domainType) {
        if (domainType == null) {
            return null;
        }

        switch (domainType) {
            case A_POSITIVE: return com.example.demo.codegen.types.BloodType.A_POSITIVE;
            case A_NEGATIVE: return com.example.demo.codegen.types.BloodType.A_NEGATIVE;
            case B_POSITIVE: return com.example.demo.codegen.types.BloodType.B_POSITIVE;
            case B_NEGATIVE: return com.example.demo.codegen.types.BloodType.B_NEGATIVE;
            case AB_POSITIVE: return com.example.demo.codegen.types.BloodType.AB_POSITIVE;
            case AB_NEGATIVE: return com.example.demo.codegen.types.BloodType.AB_NEGATIVE;
            case O_POSITIVE: return com.example.demo.codegen.types.BloodType.O_POSITIVE;
            case O_NEGATIVE: return com.example.demo.codegen.types.BloodType.O_NEGATIVE;
            default:
                throw new IllegalArgumentException("Nieznana grupa krwi: " + domainType);
        }
    }

    public static BloodType fromDTO(com.example.demo.codegen.types.BloodType dtoType) {
        if (dtoType == null) {
            return null;
        }

        switch (dtoType) {
            case A_POSITIVE: return BloodType.A_POSITIVE;
            case A_NEGATIVE: return BloodType.A_NEGATIVE;
            case B_POSITIVE: return BloodType.B_POSITIVE;
            case B_NEGATIVE: return BloodType.B_NEGATIVE;
            case AB_POSITIVE: return BloodType.AB_POSITIVE;
            case AB_NEGATIVE: return BloodType.AB_NEGATIVE;
            case O_POSITIVE: return BloodType.O_POSITIVE;
            case O_NEGATIVE: return BloodType.O_NEGATIVE;
            default:
                throw new IllegalArgumentException("Nieznana grupa krwi DTO: " + dtoType);
        }
    }
}
