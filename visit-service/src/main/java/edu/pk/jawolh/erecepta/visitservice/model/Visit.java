package edu.pk.jawolh.erecepta.visitservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Visit {
    private final UUID id;
    private final UUID doctorId;
    private final UUID patientId;
    private final Specialization specialization;
    private LocalDateTime visitTime;
    @Setter
    private VisitStatus visitStatus;

    public void setVisitTime(LocalDateTime visitTime) {
        if (LocalDateTime.now().isAfter(visitTime)) {
            throw new IllegalArgumentException("visitTime cannot be set in the past!");
        }
        this.visitTime = visitTime;
    }

}
