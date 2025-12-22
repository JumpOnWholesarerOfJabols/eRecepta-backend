package edu.pk.jawolh.erecepta.med_docs_service.mappers;

import edu.pk.jawolh.erecepta.med_docs_service.model.PrescriptionStatus;

public class PrescriptionStatusMapper {

    public static com.example.demo.codegen.types.PrescriptionStatus toDTO(PrescriptionStatus domainType) {
        if (domainType == null) {
            return null;
        }

        return switch (domainType) {
            case ISSUED -> com.example.demo.codegen.types.PrescriptionStatus.ISSUED;
            case PARTIALLY_FILLED -> com.example.demo.codegen.types.PrescriptionStatus.PARTIALLY_FILLED;
            case FILLED -> com.example.demo.codegen.types.PrescriptionStatus.FILLED;
            case CANCELLED -> com.example.demo.codegen.types.PrescriptionStatus.CANCELLED;
            case EXPIRED -> com.example.demo.codegen.types.PrescriptionStatus.EXPIRED;
        };
    }

    public static PrescriptionStatus fromDTO(com.example.demo.codegen.types.PrescriptionStatus dtoType) {
        if (dtoType == null) {
            return null;
        }

        return switch (dtoType) {
            case ISSUED -> PrescriptionStatus.ISSUED;
            case PARTIALLY_FILLED -> PrescriptionStatus.PARTIALLY_FILLED;
            case FILLED -> PrescriptionStatus.FILLED;
            case CANCELLED -> PrescriptionStatus.CANCELLED;
            case EXPIRED -> PrescriptionStatus.EXPIRED;
        };
    }
}
