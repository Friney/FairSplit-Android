package com.friney.fairsplit.network.model

data class Summary(
    val receipts: List<ReceiptSummary>,
    val total: Double
)