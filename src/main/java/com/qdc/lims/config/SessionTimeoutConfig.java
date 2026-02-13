package com.qdc.lims.config;

import com.qdc.lims.service.ConfigService;
import com.qdc.lims.ui.SessionManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration hook for session timeout settings.
 * Timeout duration comes from properties, while enabled/disabled state comes
 * from persisted system configuration.
 */
@Configuration
public class SessionTimeoutConfig {

    private final ConfigService configService;

    @Value("${qdc.session.timeout:30}")
    private long sessionTimeoutMinutes;

    public SessionTimeoutConfig(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Pushes the configured timeout (in minutes) into the global session manager
     * after dependency injection completes.
     */
    @PostConstruct
    public void configureSessionTimeout() {
        SessionManager.setSessionTimeoutMinutes(sessionTimeoutMinutes);
        boolean enabled = Boolean.parseBoolean(configService.getTrimmed("SESSION_TIMEOUT_ENABLED", "false"));
        SessionManager.setSessionTimeoutEnabled(enabled);
    }
}
