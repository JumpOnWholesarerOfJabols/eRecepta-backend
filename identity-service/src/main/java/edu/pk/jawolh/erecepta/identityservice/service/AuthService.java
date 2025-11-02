package edu.pk.jawolh.erecepta.identityservice.service;

import com.example.demo.codegen.types.Gender;
import com.example.demo.codegen.types.Role;
import edu.pk.jawolh.erecepta.identityservice.exception.UserAlreadyExistsException;
import edu.pk.jawolh.erecepta.identityservice.model.UserRole;
import edu.pk.jawolh.erecepta.identityservice.model.UserAccount;
import edu.pk.jawolh.erecepta.identityservice.model.UserGender;
import edu.pk.jawolh.erecepta.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public String registerUser(String email, String password, String pesel, Role role, Gender gender) {
        if (userRepository.existsByPeselOrEmail(email, pesel)) {
            throw new UserAlreadyExistsException("User with given PESEL or email already exists");
        }
        // TODO:
        //  -Validate PESEL
        //  -Encrypt password
        //  -Ensure role security (user cannot register as admin)
        //  -gender and role mappers

        UserRole userRole = switch (role){
            case PATIENT -> UserRole.PATIENT;
        };

        UserGender userGender = switch (gender) {
            case MALE -> UserGender.MALE;
            case FEMALE -> UserGender.FEMALE;
        };

        UserAccount account = UserAccount.builder()
                .email(email)
                .pesel(pesel)
                .hashedPassword(password)
                .role(userRole)
                .userGender(userGender)
                .verified(false)
                .build();

        UserAccount savedUser = userRepository.save(account);

        return "User registered successfully";
    }

    public String verifyAccount(String login, String code) {
        return "Account verified successfully";
    }

    public String login(String login, String password) {
        return "Login successfully";
    }

    public String resetPasswordRequest(String login) {
        return "Reset password request successfully";
    }

    public String resetPassword(String login) {
        return "Reset password successfully";
    }
}
