package com.anil.blog.repositories;

import com.anil.blog.domain.entities.ActiveToken;
import com.anil.blog.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActiveTokenRepository extends JpaRepository<ActiveToken, UUID> {
    Optional<ActiveToken> findByToken(String token);
    Optional<ActiveToken> findByUser(User user);
}