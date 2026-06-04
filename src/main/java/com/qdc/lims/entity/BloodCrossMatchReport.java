package com.qdc.lims.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Structured result data for the Blood Cross-Match special report.
 */
@Entity
@Getter
@Setter
public class BloodCrossMatchReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_order_id", nullable = false, unique = true)
    private LabOrder labOrder;

    private String recipientName;
    private String donorName;
    private String bloodBagNo;
    private String recipientAboGroup;
    private String recipientRhesusGroup;
    private String donorAboGroup;
    private String donorRhesusGroup;

    private String hbsAg;
    private String hcv;
    private String hiv;
    private String vdrl;
    private String malarialParasites;

    private String salinePhase;
    private String albuminPhase;

    @Column(columnDefinition = "text")
    private String comments;

    private String performedBy;
    private LocalDateTime performedAt;
    private LocalDateTime updatedAt;
}
