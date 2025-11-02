package edu.pk.jawolh.erecepta.identityservice.service;

import com.example.demo.codegen.types.Gender;
import com.example.demo.codegen.types.UserRole;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String registerUser(String email, String password, String pesel, UserRole role, Gender gender, Gender gender1) {
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
