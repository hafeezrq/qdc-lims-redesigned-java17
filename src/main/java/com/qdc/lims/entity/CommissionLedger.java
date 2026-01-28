package com.qdc.lims.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a commission ledger entry for a lab order and doctor.
 */
@Entity
@Data
@Table(name = "commission_ledger")
public class CommissionLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // This specific field name 'labOrder' creates the method 'setLabOrder()'
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private LabOrder labOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalBillAmount;
    @Column(precision = 7, scale = 4)
    private BigDecimal commissionPercentage;
    @Column(precision = 19, scale = 4)
    private BigDecimal calculatedAmount;
    @Column(precision = 19, scale = 4)
    private BigDecimal paidAmount;

    private LocalDate transactionDate;
    private String status;
    private LocalDate paymentDate;

    /**
     * Sets the transaction date and status before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        this.transactionDate = LocalDate.now();
        this.status = "UNPAID";
    }
}
