package com.example.nammapridechannapatna

data class Earning(
    val earningId: String,
    val artisanEmail: String,
    val toyGiId: String,
    val toyName: String,
    val amount: Int,
    val orderId: String,
    val vendorName: String,
    val timestamp: Long,
    val status: String  // CREDITED, WITHDRAWN
)