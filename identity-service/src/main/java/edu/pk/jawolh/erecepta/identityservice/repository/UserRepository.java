package edu.pk.jawolh.erecepta.identityservice.repository;

import edu.pk.jawolh.erecepta.identityservice.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserAccount, Integer> {
}
