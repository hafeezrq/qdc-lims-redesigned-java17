package com.qdc.lims.config;

import com.qdc.lims.repository.LabOrderRepository;
import com.qdc.lims.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * One-time cleanup to normalize legacy IN_PROGRESS orders back to PENDING.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OrderStatusCleanupRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusCleanupRunner.class);
    private static final String FLAG_KEY = "ORDER_STATUS_NORMALIZED_2026_02_04";

    private final LabOrderRepository labOrderRepository;
    private final ConfigService configService;

    public OrderStatusCleanupRunner(LabOrderRepository labOrderRepository, ConfigService configService) {
        this.labOrderRepository = labOrderRepository;
        this.configService = configService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        boolean alreadyDone = Boolean.parseBoolean(configService.getTrimmed(FLAG_KEY, "false"));
        if (alreadyDone) {
            return;
        }

        int updated = labOrderRepository.normalizeInProgressToPending();
        configService.set(FLAG_KEY, "true");
        log.info("Order status cleanup complete. Updated {} IN_PROGRESS orders to PENDING.", updated);
    }
}
