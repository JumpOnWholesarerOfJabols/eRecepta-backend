package edu.pk.jawolh.erecepta.identityservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {
    private UUID id;
    private UUID userId;
    private String ipAddress;
    private boolean success;
    private LocalDateTime attemptDate;
}
