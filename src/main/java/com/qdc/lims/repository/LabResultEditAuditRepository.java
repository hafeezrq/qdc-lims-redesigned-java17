package com.qdc.lims.repository;

import com.qdc.lims.entity.LabResultEditAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for result correction audit history.
 */
public interface LabResultEditAuditRepository extends JpaRepository<LabResultEditAudit, Long> {
    List<LabResultEditAudit> findAllByOrderByEditedAtDesc();

    List<LabResultEditAudit> findByLabOrderIdOrderByEditedAtDesc(Long labOrderId);
}
