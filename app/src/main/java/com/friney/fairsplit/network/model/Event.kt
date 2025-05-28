package com.friney.fairsplit.network.model

import java.io.Serializable


data class Event(
    val id: Long,
    val description: String,
    val name: String,
    val receipts: List<Receipt>
) : Serializable