package com.friney.fairsplit.network.model.receipt

import java.io.Serializable

data class ReceiptUpdate(
    val name: String,
    val userId: Long
) : Serializable