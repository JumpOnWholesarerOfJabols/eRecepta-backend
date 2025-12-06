package edu.pk.jawolh.erecepta.patientrecordservice.mapper;

import com.example.demo.codegen.types.PatientHistoryEntry;
import com.example.demo.codegen.types.RevisionType;
import edu.pk.jawolh.erecepta.patientrecordservice.model.PatientRecord;
import org.springframework.data.history.Revision;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PatientHistoryMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"));

    public static PatientHistoryEntry toDTO(Revision<Integer, PatientRecord> revision) {
        PatientRecord entity = revision.getEntity();
        var metadata = revision.getMetadata();

        return PatientHistoryEntry.newBuilder()
                .revisionNumber(metadata.getRequiredRevisionNumber())
                .revisionDate(DATE_FORMATTER.format(metadata.getRequiredRevisionInstant()))
                .revisionType(mapRevisionType(metadata.getRevisionType()))
                .patientState(PatientMapper.toDTO(entity))
                .build();
    }

    private static RevisionType mapRevisionType(org.springframework.data.history.RevisionMetadata.RevisionType revisionType) {
        return switch (revisionType) {
            case INSERT -> RevisionType.ADD;
            case UPDATE -> RevisionType.MOD;
            case DELETE -> RevisionType.DEL;
            case UNKNOWN -> null;
        };
    }
}