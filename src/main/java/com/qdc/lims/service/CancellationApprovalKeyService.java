package com.qdc.lims.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Manages the system-wide cancellation approval key used to authorize
 * order cancellation requests.
 */
@Service
public class CancellationApprovalKeyService {

    private static final String CANCEL_APPROVAL_KEY_HASH = "CANCEL_APPROVAL_KEY_HASH";
    private static final int MIN_KEY_LENGTH = 6;

    private final ConfigService configService;
    private final PasswordEncoder passwordEncoder;

    public CancellationApprovalKeyService(ConfigService configService, PasswordEncoder passwordEncoder) {
        this.configService = configService;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isKeyConfigured() {
        return !configService.getTrimmed(CANCEL_APPROVAL_KEY_HASH).isBlank();
    }

    public void setKey(String rawKey) {
        String normalized = normalize(rawKey);
        if (normalized.length() < MIN_KEY_LENGTH) {
            throw new IllegalArgumentException("Cancellation key must be at least " + MIN_KEY_LENGTH + " characters.");
        }
        String hash = passwordEncoder.encode(normalized);
        configService.set(CANCEL_APPROVAL_KEY_HASH, hash);
    }

    public boolean verifyKey(String rawKey) {
        String normalized = normalize(rawKey);
        if (normalized.isBlank()) {
            return false;
        }
        String storedHash = configService.getTrimmed(CANCEL_APPROVAL_KEY_HASH);
        if (storedHash.isBlank()) {
            return false;
        }
        return passwordEncoder.matches(normalized, storedHash);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
