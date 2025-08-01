package com.friney.fairsplit.network.model.event

import com.friney.fairsplit.network.model.receipt.Receipt
import java.io.Serializable

data class Event(
    val id: Long,
    val description: String,
    val name: String,
    val receipts: List<Receipt>
) : Serializable