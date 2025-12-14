package edu.pk.jawolh.erecepta.identityservice.bootstrap;

import edu.pk.jawolh.erecepta.identityservice.model.UserAccount;
import edu.pk.jawolh.erecepta.identityservice.repository.UserRepository;
import edu.pk.jawolh.erecepta.identityservice.validation.RegisterValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RegisterValidator registerValidator;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties properties;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            if (properties.getPesel() == null ||
                    properties.getDateOfBirth() == null ||
                    properties.getGender() == null ||
                    properties.getEmail() == null ||
                    properties.getFirstName() == null ||
                    properties.getLastName() == null ||
                    properties.getPhone() == null ||
                    properties.getPassword() == null) {
                return;
            }

            registerValidator.validateRegistrationData(
                    properties.getPesel(),
                    properties.getDateOfBirth(),
                    properties.getGender(),
                    properties.getEmail(),
                    properties.getFirstName(),
                    properties.getLastName(),
                    properties.getPhone(),
                    properties.getPassword()
            );

            UserAccount user = UserAccount.builder()
                    .email(properties.getEmail())
                    .verified(true)
                    .phoneNumber(properties.getPhone())
                    .firstName(properties.getFirstName())
                    .lastName(properties.getLastName())
                    .pesel(properties.getPesel())
                    .dateOfBirth(properties.getDateOfBirth())
                    .role(edu.pk.jawolh.erecepta.common.user.enums.UserRole.ADMINISTRATOR)
                    .userGender(properties.getGender())
                    .hashedPassword(passwordEncoder.encode(properties.getPassword()))
                    .build();

            userRepository.save(user);
        }
    }
}
