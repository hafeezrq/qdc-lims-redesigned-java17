package com.qdc.lims.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Legacy/alternate test category entity. The current UI primarily uses
 * {@link Department}, but this table may still be referenced by reporting or
 * migration scripts.
 */
@Entity
@Table(name = "test_categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "name", "department_id" })
})
@Data
public class TestCategory {

    /**
     * Surrogate primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique category name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Owning department for the category.
     */
    @ManyToOne
    @JoinColumn(name = "department_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Department department;

    /**
     * Optional category description.
     */
    private String description;

    /**
     * Soft-active flag.
     */
    @Column(name = "is_active")
    private boolean active = true;

    /**
     * Tests assigned to this category.
     */
    @OneToMany(mappedBy = "category")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<TestDefinition> testDefinitions;
}
