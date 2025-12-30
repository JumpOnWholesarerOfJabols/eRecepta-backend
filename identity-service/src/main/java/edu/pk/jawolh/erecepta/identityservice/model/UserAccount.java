package edu.pk.jawolh.erecepta.identityservice.model;

import edu.pk.jawolh.erecepta.common.user.enums.UserGender;
import edu.pk.jawolh.erecepta.common.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    private UUID id;
    private String email;
    private String pesel;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private UserRole role;
    private UserGender userGender;
    private String hashedPassword;
    private boolean verified;
}
