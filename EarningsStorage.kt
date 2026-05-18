package com.example.nammapridechannapatna

import android.content.Context
import android.content.SharedPreferences
import kotlin.random.Random

class EarningsStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ChannapatnaEarnings", Context.MODE_PRIVATE)

    // ================================
    // ✅ CREDIT MONEY TO ARTISAN
    // (Called when vendor purchases toy)
    // ================================
    fun creditEarning(
        artisanEmail: String,
        toyGiId: String,
        toyName: String,
        amount: Int,
        orderId: String,
        vendorName: String
    ): String {
        val earningId = "EARN${System.currentTimeMillis().toString().takeLast(8)}"
        val timestamp = System.currentTimeMillis()

        // Format: id|email|giId|name|amount|orderId|vendor|time|status;
        val newEarning = "$earningId|$artisanEmail|$toyGiId|$toyName|" +
                "$amount|$orderId|$vendorName|$timestamp|CREDITED;"

        val allEarnings = prefs.getString("all_earnings", "") ?: ""
        prefs.edit().putString("all_earnings", allEarnings + newEarning).commit()

        // Update available balance
        val currentBalance = getAvailableBalance(artisanEmail)
        val newBalance = currentBalance + amount
        prefs.edit().putInt("balance_$artisanEmail", newBalance).commit()

        // Update total earnings
        val totalEarnings = getTotalEarnings(artisanEmail)
        prefs.edit().putInt("total_$artisanEmail", totalEarnings + amount).commit()

        return earningId
    }

    // ================================
    // ✅ GET ALL EARNINGS FOR ARTISAN
    // ================================
    fun getEarningsForArtisan(artisanEmail: String): List<Earning> {
        val data = prefs.getString("all_earnings", "") ?: ""
        if (data.isEmpty()) return emptyList()

        return data.split(";").mapNotNull { entry ->
            val cleaned = entry.trim()
            if (cleaned.isEmpty()) return@mapNotNull null

            val parts = cleaned.split("|")
            if (parts.size >= 9 && parts[1] == artisanEmail) {
                Earning(
                    earningId = parts[0],
                    artisanEmail = parts[1],
                    toyGiId = parts[2],
                    toyName = parts[3],
                    amount = parts[4].toIntOrNull() ?: 0,
                    orderId = parts[5],
                    vendorName = parts[6],
                    timestamp = parts[7].toLongOrNull() ?: 0L,
                    status = parts[8]
                )
            } else null
        }.reversed()  // Latest first
    }

    // ================================
    // ✅ GET AVAILABLE BALANCE
    // ================================
    fun getAvailableBalance(artisanEmail: String): Int =
        prefs.getInt("balance_$artisanEmail", 0)

    // ================================
    // ✅ GET TOTAL EARNINGS (lifetime)
    // ================================
    fun getTotalEarnings(artisanEmail: String): Int =
        prefs.getInt("total_$artisanEmail", 0)

    // ================================
    // ✅ GET TOTAL WITHDRAWN
    // ================================
    fun getTotalWithdrawn(artisanEmail: String): Int =
        prefs.getInt("withdrawn_$artisanEmail", 0)

    // ================================
    // ✅ GET TOTAL ORDERS COUNT
    // ================================
    fun getTotalOrdersCount(artisanEmail: String): Int =
        getEarningsForArtisan(artisanEmail).size

    // ================================
    // ✅ WITHDRAW MONEY
    // ================================
    fun withdrawMoney(
        artisanEmail: String,
        amount: Int,
        bankName: String,
        accountNumber: String,
        ifscCode: String,
        accountHolderName: String
    ): String? {
        val balance = getAvailableBalance(artisanEmail)
        if (amount > balance) return null
        if (amount < 100) return null  // Minimum withdrawal

        val withdrawalId = "WD${System.currentTimeMillis().toString().takeLast(8)}"
        val timestamp = System.currentTimeMillis()

        // Format: id|email|amount|bank|account|ifsc|holder|time|status;
        val newWithdrawal = "$withdrawalId|$artisanEmail|$amount|$bankName|" +
                "$accountNumber|$ifscCode|$accountHolderName|$timestamp|COMPLETED;"

        val allWithdrawals = prefs.getString("all_withdrawals", "") ?: ""
        prefs.edit().putString("all_withdrawals", allWithdrawals + newWithdrawal).commit()

        // Update balance
        prefs.edit().putInt("balance_$artisanEmail", balance - amount).commit()

        // Update total withdrawn
        val totalWithdrawn = getTotalWithdrawn(artisanEmail)
        prefs.edit().putInt("withdrawn_$artisanEmail", totalWithdrawn + amount).commit()

        return withdrawalId
    }

    // ================================
    // ✅ GET WITHDRAWAL HISTORY
    // ================================
    fun getWithdrawalsForArtisan(artisanEmail: String): List<Withdrawal> {
        val data = prefs.getString("all_withdrawals", "") ?: ""
        if (data.isEmpty()) return emptyList()

        return data.split(";").mapNotNull { entry ->
            val cleaned = entry.trim()
            if (cleaned.isEmpty()) return@mapNotNull null

            val parts = cleaned.split("|")
            if (parts.size >= 9 && parts[1] == artisanEmail) {
                Withdrawal(
                    withdrawalId = parts[0],
                    artisanEmail = parts[1],
                    amount = parts[2].toIntOrNull() ?: 0,
                    bankName = parts[3],
                    accountNumber = parts[4],
                    ifscCode = parts[5],
                    accountHolderName = parts[6],
                    timestamp = parts[7].toLongOrNull() ?: 0L,
                    status = parts[8]
                )
            } else null
        }.reversed()
    }

    // ================================
    // ✅ SAVE BANK DETAILS
    // ================================
    fun saveBankDetails(
        artisanEmail: String,
        bankName: String,
        accountNumber: String,
        ifscCode: String,
        accountHolderName: String,
        upiId: String
    ) {
        prefs.edit().apply {
            putString("bank_name_$artisanEmail", bankName)
            putString("account_number_$artisanEmail", accountNumber)
            putString("ifsc_code_$artisanEmail", ifscCode)
            putString("account_holder_$artisanEmail", accountHolderName)
            putString("upi_id_$artisanEmail", upiId)
            commit()
        }
    }

    // ================================
    // ✅ GET BANK DETAILS
    // ================================
    fun getBankName(artisanEmail: String): String =
        prefs.getString("bank_name_$artisanEmail", "") ?: ""

    fun getAccountNumber(artisanEmail: String): String =
        prefs.getString("account_number_$artisanEmail", "") ?: ""

    fun getIfscCode(artisanEmail: String): String =
        prefs.getString("ifsc_code_$artisanEmail", "") ?: ""

    fun getAccountHolderName(artisanEmail: String): String =
        prefs.getString("account_holder_$artisanEmail", "") ?: ""

    fun getUpiId(artisanEmail: String): String =
        prefs.getString("upi_id_$artisanEmail", "") ?: ""

    fun hasBankDetails(artisanEmail: String): Boolean =
        getAccountNumber(artisanEmail).isNotEmpty()
}