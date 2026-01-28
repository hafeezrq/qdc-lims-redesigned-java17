package com.qdc.lims.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents the consumption recipe for a test.
 * E.g., A CBC test consumes 1 Vacuum Tube.
 */
@Entity
@Data
@Table(name = "test_recipes")
public class TestRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id", nullable = false)
    private TestDefinition test;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity; // How much is consumed per test instance.

}
