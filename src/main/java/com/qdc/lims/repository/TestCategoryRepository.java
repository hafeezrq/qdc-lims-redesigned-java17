package com.qdc.lims.repository;

import com.qdc.lims.entity.TestCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qdc.lims.entity.Department;
import java.util.Optional;
import java.util.List;

/**
 * Repository for {@link TestCategory} entries.
 */
@Repository
public interface TestCategoryRepository extends JpaRepository<TestCategory, Long> {

    /**
     * Finds a category by its unique name.
     *
     * @param name category name
     * @return matching category, if present
     */
    Optional<TestCategory> findByName(String name);

    /**
     * Finds categories under a department.
     *
     * @param department owning department
     * @return categories for the department
     */
    List<TestCategory> findByDepartment(Department department);

    /**
     * Finds a category by name within a department.
     *
     * @param name category name
     * @param department owning department
     * @return matching category, if present
     */
    Optional<TestCategory> findByNameAndDepartment(String name, Department department);
}
