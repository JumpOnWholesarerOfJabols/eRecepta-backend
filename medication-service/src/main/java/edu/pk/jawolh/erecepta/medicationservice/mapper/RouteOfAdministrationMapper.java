package edu.pk.jawolh.erecepta.medicationservice.mapper;

import edu.pk.jawolh.erecepta.medicationservice.model.RouteOfAdministration;

public class RouteOfAdministrationMapper {

    public static com.example.demo.codegen.types.RouteOfAdministration toDTO(RouteOfAdministration domainType) {
        if (domainType == null) {
            return null;
        }

        return switch (domainType) {
            case ORAL -> com.example.demo.codegen.types.RouteOfAdministration.ORAL;
            case INTRAVENOUS -> com.example.demo.codegen.types.RouteOfAdministration.INTRAVENOUS;
            case INTRAMUSCULAR -> com.example.demo.codegen.types.RouteOfAdministration.INTRAMUSCULAR;
            case TOPICAL -> com.example.demo.codegen.types.RouteOfAdministration.TOPICAL;
            case INHALATION -> com.example.demo.codegen.types.RouteOfAdministration.INHALATION;
            case RECTAL -> com.example.demo.codegen.types.RouteOfAdministration.RECTAL;
        };
    }

    public static RouteOfAdministration fromDTO(com.example.demo.codegen.types.RouteOfAdministration dtoType) {
        if (dtoType == null) {
            return null;
        }

        return switch (dtoType) {
            case ORAL -> RouteOfAdministration.ORAL;
            case INTRAVENOUS -> RouteOfAdministration.INTRAVENOUS;
            case INTRAMUSCULAR -> RouteOfAdministration.INTRAMUSCULAR;
            case TOPICAL -> RouteOfAdministration.TOPICAL;
            case INHALATION -> RouteOfAdministration.INHALATION;
            case RECTAL -> RouteOfAdministration.RECTAL;
        };
    }
}