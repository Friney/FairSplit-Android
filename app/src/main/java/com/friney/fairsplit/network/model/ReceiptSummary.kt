package com.friney.fairsplit.network.model

data class ReceiptSummary(
    val debts: List<Debt>,
    val name: String,
    val total: Double
)