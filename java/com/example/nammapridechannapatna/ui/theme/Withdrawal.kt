package com.example.nammapridechannapatna

data class Withdrawal(
    val withdrawalId: String,
    val artisanEmail: String,
    val amount: Int,
    val bankName: String,
    val accountNumber: String,
    val ifscCode: String,
    val accountHolderName: String,
    val timestamp: Long,
    val status: String  // PENDING, COMPLETED, FAILED
)