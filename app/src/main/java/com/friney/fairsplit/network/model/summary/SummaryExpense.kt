package com.friney.fairsplit.network.model.summary

import java.math.BigDecimal

data class SummaryExpense(
    val name: String,
    val payerInfos: List<PayerInfo>,
    val total: BigDecimal
)