package com.qdc.lims.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for creating a new purchase request.
 *
 * @param supplierId the ID of the supplier
 * @param invoiceNumber the invoice number for the purchase
 * @param items the list of items being purchased
 * @param amountPaidNow the amount paid at the time of purchase
 * @param paymentMode the mode of payment (e.g., "Cash", "Cheque", "Online")
 */
public record PurchaseRequest(
        Long supplierId,
        String invoiceNumber,
        List<PurchaseItemDTO> items,
        BigDecimal amountPaidNow, // e.g. 5000.0
        String paymentMode // "Cash", "Cheque", "Online"
) {
}
