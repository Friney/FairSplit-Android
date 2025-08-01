package com.friney.fairsplit.network.model.receipt

import java.io.Serializable

data class ReceiptCreate(
    val name: String,
    val userId: Long
) : Serializable