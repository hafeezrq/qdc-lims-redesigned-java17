package com.qdc.lims.repository;

import com.qdc.lims.entity.BloodCrossMatchReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BloodCrossMatchReportRepository extends JpaRepository<BloodCrossMatchReport, Long> {
    Optional<BloodCrossMatchReport> findByLabOrderId(Long labOrderId);
}
