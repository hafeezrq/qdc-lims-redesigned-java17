package com.qdc.lims.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Immutable-style audit row for each completed-result correction.
 */
@Entity
@Table(name = "lab_result_edit_audit")
@Getter
@Setter
public class LabResultEditAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private LabOrder labOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "result_id", nullable = false)
    private LabResult labResult;

    private String testName;

    @Column(length = 1000)
    private String previousValue;

    @Column(length = 1000)
    private String newValue;

    private String previousRemarks;
    private String newRemarks;
    private boolean previousAbnormal;
    private boolean newAbnormal;

    private String editedBy;
    private LocalDateTime editedAt;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean reportDeliveredAtEdit = false;
}
