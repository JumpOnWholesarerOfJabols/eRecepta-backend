package edu.pk.jawolh.erecepta.identityservice.repository;

import edu.pk.jawolh.erecepta.identityservice.model.UserVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationCodeRepository extends JpaRepository<UserVerificationCode, UUID> {
    Optional<UserVerificationCode> findByPeselOrEmail(String pesel, String email);
    boolean existsByPeselOrEmail(String pesel, String email);
    void deleteAllByPeselOrEmail(String pesel, String email);
}
