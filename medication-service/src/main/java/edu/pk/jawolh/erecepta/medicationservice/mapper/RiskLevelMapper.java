package edu.pk.jawolh.erecepta.medicationservice.mapper;

import edu.pk.jawolh.erecepta.medicationservice.model.RiskLevel;

public class RiskLevelMapper {

    public static com.example.demo.codegen.types.RiskLevel toDTO(RiskLevel domainType) {
        if (domainType == null) {
            return null;
        }

        return switch (domainType) {
            case LOW -> com.example.demo.codegen.types.RiskLevel.LOW;
            case MODERATE -> com.example.demo.codegen.types.RiskLevel.MODERATE;
            case HIGH -> com.example.demo.codegen.types.RiskLevel.HIGH;
            case CONTRAINDICATED -> com.example.demo.codegen.types.RiskLevel.CONTRAINDICATED;
        };
    }

    public static RiskLevel fromDTO(com.example.demo.codegen.types.RiskLevel dtoType) {
        if (dtoType == null) {
            return null;
        }

        return switch (dtoType) {
            case LOW -> RiskLevel.LOW;
            case MODERATE -> RiskLevel.MODERATE;
            case HIGH -> RiskLevel.HIGH;
            case CONTRAINDICATED -> RiskLevel.CONTRAINDICATED;
        };
    }
}