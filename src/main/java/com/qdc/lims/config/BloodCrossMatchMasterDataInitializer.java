package com.qdc.lims.config;

import com.qdc.lims.entity.Department;
import com.qdc.lims.entity.TestCategory;
import com.qdc.lims.entity.TestDefinition;
import com.qdc.lims.repository.DepartmentRepository;
import com.qdc.lims.repository.TestCategoryRepository;
import com.qdc.lims.repository.TestDefinitionRepository;
import com.qdc.lims.service.BloodCrossMatchReportService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class BloodCrossMatchMasterDataInitializer implements ApplicationRunner {

    private final DepartmentRepository departmentRepository;
    private final TestCategoryRepository testCategoryRepository;
    private final TestDefinitionRepository testDefinitionRepository;

    public BloodCrossMatchMasterDataInitializer(DepartmentRepository departmentRepository,
            TestCategoryRepository testCategoryRepository,
            TestDefinitionRepository testDefinitionRepository) {
        this.departmentRepository = departmentRepository;
        this.testCategoryRepository = testCategoryRepository;
        this.testDefinitionRepository = testDefinitionRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Department department = departmentRepository.findByName("Blood Bank").orElseGet(() -> {
            Department created = new Department();
            created.setName("Blood Bank");
            created.setCode("BBK");
            created.setActive(true);
            return departmentRepository.save(created);
        });

        TestCategory category = testCategoryRepository.findByNameAndDepartment("Cross-Match", department)
                .orElseGet(() -> {
                    TestCategory created = new TestCategory();
                    created.setName("Cross-Match");
                    created.setDescription("Blood cross-match and compatibility testing");
                    created.setDepartment(department);
                    created.setActive(true);
                    return testCategoryRepository.save(created);
                });

        TestDefinition test = testDefinitionRepository.findByShortCode(BloodCrossMatchReportService.TEST_SHORT_CODE);
        if (test == null) {
            test = new TestDefinition();
        }
        test.setTestName(BloodCrossMatchReportService.TEST_NAME);
        test.setShortCode(BloodCrossMatchReportService.TEST_SHORT_CODE);
        test.setDepartment(department);
        test.setCategory(category);
        test.setUnit(null);
        if (test.getPrice() == null) {
            test.setPrice(BigDecimal.ZERO);
        }
        test.setMinRange(null);
        test.setMaxRange(null);
        test.setActive(true);
        test.setSkipWorklist(false);
        testDefinitionRepository.save(test);
    }
}
