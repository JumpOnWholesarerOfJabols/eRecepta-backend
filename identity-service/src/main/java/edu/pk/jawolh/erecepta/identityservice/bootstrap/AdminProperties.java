package edu.pk.jawolh.erecepta.identityservice.bootstrap;

import edu.pk.jawolh.erecepta.common.user.enums.UserGender;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Data
@ConfigurationProperties(prefix = "application.admin", ignoreInvalidFields = true)
public class AdminProperties {
    private String email;
    private String password;
    private String pesel;
    private String phone;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private UserGender gender;
}
