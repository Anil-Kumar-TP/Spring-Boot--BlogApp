package com.anil.blog.repositories;

import com.anil.blog.domain.entities.User;
import com.anil.blog.domain.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUser(User user); //Retrieves the latest token for a given user (if any).
}