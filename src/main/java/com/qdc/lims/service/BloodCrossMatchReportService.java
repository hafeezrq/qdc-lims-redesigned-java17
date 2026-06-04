package com.qdc.lims.service;

import com.qdc.lims.entity.BloodCrossMatchReport;
import com.qdc.lims.entity.LabOrder;
import com.qdc.lims.entity.LabResult;
import com.qdc.lims.repository.BloodCrossMatchReportRepository;
import com.qdc.lims.repository.LabOrderRepository;
import com.qdc.lims.repository.LabResultRepository;
import com.qdc.lims.ui.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BloodCrossMatchReportService {

    public static final String TEST_SHORT_CODE = "BCROSS";
    public static final String TEST_NAME = "Blood Cross-Match";

    private final BloodCrossMatchReportRepository reportRepository;
    private final LabOrderRepository labOrderRepository;
    private final LabResultRepository labResultRepository;
    private final CurrentUserProvider currentUserProvider;

    public BloodCrossMatchReportService(BloodCrossMatchReportRepository reportRepository,
            LabOrderRepository labOrderRepository,
            LabResultRepository labResultRepository,
            CurrentUserProvider currentUserProvider) {
        this.reportRepository = reportRepository;
        this.labOrderRepository = labOrderRepository;
        this.labResultRepository = labResultRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public Optional<BloodCrossMatchReport> findByOrderId(Long orderId) {
        if (orderId == null) {
            return Optional.empty();
        }
        return reportRepository.findByLabOrderId(orderId);
    }

    @Transactional
    public BloodCrossMatchReport getOrCreateForOrder(LabOrder order) {
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("Order is required.");
        }
        return reportRepository.findByLabOrderId(order.getId()).orElseGet(() -> {
            LabOrder managedOrder = labOrderRepository.findById(order.getId())
                    .orElseThrow(() -> new RuntimeException("Order not found: " + order.getId()));
            BloodCrossMatchReport report = new BloodCrossMatchReport();
            report.setLabOrder(managedOrder);
            if (managedOrder.getPatient() != null) {
                report.setRecipientName(managedOrder.getPatient().getFullName());
            }
            return reportRepository.save(report);
        });
    }

    @Transactional
    public BloodCrossMatchReport saveForOrder(LabOrder order, BloodCrossMatchReport form) {
        return saveForOrder(order, form, null);
    }

    @Transactional
    public BloodCrossMatchReport saveForOrder(LabOrder order, BloodCrossMatchReport form, String editReason) {
        BloodCrossMatchReport report = getOrCreateForOrder(order);
        boolean wasCompletedBeforeSave = report.getLabOrder() != null
                && "COMPLETED".equals(report.getLabOrder().getStatus());
        String previousSnapshot = snapshot(report);
        copyFields(form, report);
        boolean changed = !previousSnapshot.equals(snapshot(report));
        String username = currentUserProvider.getUsername();
        LocalDateTime now = LocalDateTime.now();
        if (report.getPerformedAt() == null) {
            report.setPerformedAt(now);
            report.setPerformedBy(username);
        }
        report.setUpdatedAt(now);
        BloodCrossMatchReport saved = reportRepository.save(report);

        LabResult result = findCrossMatchResult(saved.getLabOrder());
        if (result != null) {
            result.setResultValue(buildSummary(saved));
            result.setStatus("COMPLETED");
            result.setPerformedBy(username);
            result.setPerformedAt(now);
            result.setAbnormal(false);
            result.setRemarks("");
            labResultRepository.save(result);
        }
        refreshOrderStatus(saved.getLabOrder(), now);
        markOrderEditedIfNeeded(saved.getLabOrder(), changed, wasCompletedBeforeSave, editReason, username, now);

        return saved;
    }

    public boolean isComplete(BloodCrossMatchReport report) {
        return report != null
                && hasText(report.getRecipientName())
                && hasText(report.getDonorName())
                && hasText(report.getBloodBagNo())
                && hasText(report.getRecipientAboGroup())
                && hasText(report.getRecipientRhesusGroup())
                && hasText(report.getDonorAboGroup())
                && hasText(report.getDonorRhesusGroup())
                && hasText(report.getHbsAg())
                && hasText(report.getHcv())
                && hasText(report.getHiv())
                && hasText(report.getVdrl())
                && hasText(report.getMalarialParasites())
                && hasText(report.getSalinePhase())
                && hasText(report.getAlbuminPhase());
    }

    public boolean isCrossMatchOrder(LabOrder order) {
        return findCrossMatchResult(order) != null;
    }

    public boolean isCrossMatchResult(LabResult result) {
        if (result == null || result.getTestDefinition() == null) {
            return false;
        }
        String shortCode = result.getTestDefinition().getShortCode();
        String testName = result.getTestDefinition().getTestName();
        return TEST_SHORT_CODE.equalsIgnoreCase(trim(shortCode))
                || TEST_NAME.equalsIgnoreCase(trim(testName));
    }

    private LabResult findCrossMatchResult(LabOrder order) {
        if (order == null || order.getResults() == null) {
            return null;
        }
        return order.getResults().stream()
                .filter(this::isCrossMatchResult)
                .findFirst()
                .orElse(null);
    }

    private void copyFields(BloodCrossMatchReport source, BloodCrossMatchReport target) {
        target.setRecipientName(trim(source.getRecipientName()));
        target.setDonorName(trim(source.getDonorName()));
        target.setBloodBagNo(trim(source.getBloodBagNo()));
        target.setRecipientAboGroup(trim(source.getRecipientAboGroup()));
        target.setRecipientRhesusGroup(trim(source.getRecipientRhesusGroup()));
        target.setDonorAboGroup(trim(source.getDonorAboGroup()));
        target.setDonorRhesusGroup(trim(source.getDonorRhesusGroup()));
        target.setHbsAg(trim(source.getHbsAg()));
        target.setHcv(trim(source.getHcv()));
        target.setHiv(trim(source.getHiv()));
        target.setVdrl(trim(source.getVdrl()));
        target.setMalarialParasites(trim(source.getMalarialParasites()));
        target.setSalinePhase(trim(source.getSalinePhase()));
        target.setAlbuminPhase(trim(source.getAlbuminPhase()));
        target.setComments(trim(source.getComments()));
    }

    private void refreshOrderStatus(LabOrder order, LocalDateTime now) {
        if (order == null || order.getId() == null) {
            return;
        }
        LabOrder managedOrder = labOrderRepository.findById(order.getId()).orElse(null);
        if (managedOrder == null || managedOrder.getResults() == null || managedOrder.getResults().isEmpty()) {
            return;
        }
        boolean allTestsDone = managedOrder.getResults().stream()
                .allMatch(result -> result.getResultValue() != null && !result.getResultValue().trim().isEmpty());
        boolean anyTestStarted = managedOrder.getResults().stream()
                .anyMatch(result -> result.getResultValue() != null && !result.getResultValue().trim().isEmpty()
                        || result.getPerformedAt() != null
                        || (result.getPerformedBy() != null && !result.getPerformedBy().trim().isEmpty()));
        if (anyTestStarted && managedOrder.getLabStartedAt() == null) {
            managedOrder.setLabStartedAt(now);
        }
        if (allTestsDone) {
            managedOrder.setStatus("COMPLETED");
        } else if (anyTestStarted || managedOrder.getLabStartedAt() != null) {
            managedOrder.setStatus("IN_PROGRESS");
        } else {
            managedOrder.setStatus("PENDING");
        }
        labOrderRepository.save(managedOrder);
    }

    private void markOrderEditedIfNeeded(LabOrder order, boolean changed, boolean wasCompletedBeforeSave,
            String editReason, String username, LocalDateTime now) {
        if (!changed || !wasCompletedBeforeSave || order == null || order.getId() == null) {
            return;
        }
        LabOrder managedOrder = labOrderRepository.findById(order.getId()).orElse(null);
        if (managedOrder == null || !"COMPLETED".equals(managedOrder.getStatus())) {
            return;
        }
        managedOrder.setResultsEdited(true);
        managedOrder.setResultsEditedAt(now);
        managedOrder.setResultsEditedBy(username);
        if (editReason != null && !editReason.trim().isEmpty()) {
            managedOrder.setResultsEditReason(editReason.trim());
        }
        if (managedOrder.isReportDelivered()) {
            managedOrder.setReprintRequired(true);
        }
        labOrderRepository.save(managedOrder);
    }

    private String snapshot(BloodCrossMatchReport report) {
        if (report == null) {
            return "";
        }
        return String.join("|",
                trim(report.getRecipientName()),
                trim(report.getDonorName()),
                trim(report.getBloodBagNo()),
                trim(report.getRecipientAboGroup()),
                trim(report.getRecipientRhesusGroup()),
                trim(report.getDonorAboGroup()),
                trim(report.getDonorRhesusGroup()),
                trim(report.getHbsAg()),
                trim(report.getHcv()),
                trim(report.getHiv()),
                trim(report.getVdrl()),
                trim(report.getMalarialParasites()),
                trim(report.getSalinePhase()),
                trim(report.getAlbuminPhase()),
                trim(report.getComments()));
    }

    private String buildSummary(BloodCrossMatchReport report) {
        String saline = hasText(report.getSalinePhase()) ? report.getSalinePhase() : "-";
        String albumin = hasText(report.getAlbuminPhase()) ? report.getAlbuminPhase() : "-";
        return "Saline: " + saline + "; Albumin: " + albumin;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
