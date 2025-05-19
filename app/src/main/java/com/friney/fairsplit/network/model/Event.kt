package com.friney.fairsplit.network.model

data class Event(
    val id: Long,
    val description: String,
    val name: String,
    val receipts: List<Receipt>
)