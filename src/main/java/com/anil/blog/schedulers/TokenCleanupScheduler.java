package com.anil.blog.schedulers;

import com.anil.blog.repositories.RevokedTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final RevokedTokenRepository revokedTokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // Runs daily at midnight
    public void cleanRevokedTokens() {
        log.info("Starting cleanup of expired revoked tokens");
        int deletedCount = revokedTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        log.info("Deleted {} expired revoked tokens", deletedCount);
    }
}