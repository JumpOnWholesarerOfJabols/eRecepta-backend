package edu.pk.jawolh.erecepta.identityservice.service;

import com.example.demo.codegen.types.Gender;
import edu.pk.jawolh.erecepta.identityservice.dto.JwtTokenDTO;
import edu.pk.jawolh.erecepta.identityservice.mapper.GenderMapper;
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
    private final VerificationCodeService verificationCodeService;
    private final ResetPasswordCodeService resetPasswordCodeService;
    private final JwtService jwtService;

    public String registerUser(String email, String password, String pesel, Gender gender) {
        if (userRepository.existsByPeselOrEmail(email, pesel)) {
            throw new IllegalArgumentException("User with given PESEL or email already exists");
        }
        // TODO:
        //  -Validate PESEL
        //  -Validate data
        //  -Encrypt password

        UserAccount account = UserAccount.builder()
                .email(email)
                .pesel(pesel)
                .hashedPassword(password)
                .role(UserRole.PATIENT)
                .userGender(GenderMapper.mapGender(gender))
                .verified(false)
                .build();

        UserAccount savedUser = userRepository.save(account);

        // TODO: mail with verification code
        String verificationCode = verificationCodeService.generateVerificationCode(savedUser.getEmail(), savedUser.getPesel());

        return "User registered successfully";
    }

    public String verifyAccount(String login, String code) {
        if (!userRepository.existsByPeselOrEmail(login, login))
            throw new IllegalArgumentException("User with given PESEL or email does not exist");

        verificationCodeService.verifyVerificationCode(login, login, code);

        return "Account verified successfully";
    }

    public JwtTokenDTO login(String login, String password) {
        UserAccount account = userRepository.findByPeselOrEmail(login, login)
                .orElseThrow(
                        ()-> new IllegalArgumentException("User with given PESEL or email does not exist"));

        if (!password.equals(account.getHashedPassword()))
            throw new IllegalArgumentException("Wrong password");

        if (!account.isVerified())
            throw new IllegalStateException("Account is not verified");

        return jwtService.generateToken(account.getId());
    }

    public String resetPasswordRequest(String login) {
        UserAccount account = userRepository.findByPeselOrEmail(login, login)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // TODO: mail with reset password code
        String code = resetPasswordCodeService.generateResetPasswordCode(account.getEmail(), account.getPesel());

        return "Reset password request successfully";
    }

    public String resetPassword(String login, String password, String code) {
        UserAccount account = userRepository.findByPeselOrEmail(login, login)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        resetPasswordCodeService.verifyResetPasswordCode(login, login, code);

        //TODO: Encrypt password
        account.setHashedPassword(password);
        userRepository.save(account);

        return "Reset password successfully";
    }

    public String sendVerificationCode(String login) {
        UserAccount account = userRepository.findByPeselOrEmail(login, login)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        verificationCodeService.generateVerificationCode(account.getEmail(), account.getPesel());

        return "Verification code sent";
    }
}
