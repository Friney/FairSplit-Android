package com.friney.fairsplit.network.model.summary

import java.math.BigDecimal

data class Summary(
    val debts: List<Debt>,
    val payerInfos: List<PayerInfo>,
    val receipts: List<SummaryReceipt>,
    val total: BigDecimal
)