package edu.pk.jawolh.erecepta.identityservice.repository;

import edu.pk.jawolh.erecepta.identityservice.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, UUID> {
    boolean existsByPeselOrEmail(String pesel, String email);
    Optional<UserAccount> findByPeselOrEmail(String pesel, String email);
}
