package com.example.nammapridechannapatna

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class WithdrawActivity : ComponentActivity() {

    private lateinit var earningsStorage: EarningsStorage
    private lateinit var userStorage: UserStorage
    private lateinit var editAmount: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw)
        overridePendingTransition(0, 0)

        earningsStorage = EarningsStorage(this)
        userStorage = UserStorage(this)

        val btnBack: TextView = findViewById(R.id.btnBack)
        editAmount = findViewById(R.id.editWithdrawAmount)
        val btnConfirm: Button = findViewById(R.id.btnConfirmWithdraw)
        val btnEditBank: TextView = findViewById(R.id.btnEditBank)
        val btnAddBank: Button = findViewById(R.id.btnAddBank)
        val btn500: TextView = findViewById(R.id.btnAmount500)
        val btn1000: TextView = findViewById(R.id.btnAmount1000)
        val btnAll: TextView = findViewById(R.id.btnAmountAll)

        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        btnEditBank.setOnClickListener {
            startActivity(Intent(this, BankDetailsActivity::class.java))
            overridePendingTransition(0, 0)
        }

        btnAddBank.setOnClickListener {
            startActivity(Intent(this, BankDetailsActivity::class.java))
            overridePendingTransition(0, 0)
        }

        btn500.setOnClickListener { editAmount.setText("500") }
        btn1000.setOnClickListener { editAmount.setText("1000") }
        btnAll.setOnClickListener {
            val balance = earningsStorage.getAvailableBalance(userStorage.getCurrentUserEmail())
            editAmount.setText(balance.toString())
        }

        btnConfirm.setOnClickListener { processWithdrawal() }

        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val email = userStorage.getCurrentUserEmail()
        val balance = earningsStorage.getAvailableBalance(email)

        val txtBalance: TextView = findViewById(R.id.txtWithdrawBalance)
        txtBalance.text = "₹$balance"

        val layoutBankInfo: LinearLayout = findViewById(R.id.layoutBankInfo)
        val layoutNoBank: LinearLayout = findViewById(R.id.layoutNoBank)

        if (earningsStorage.hasBankDetails(email)) {
            layoutBankInfo.visibility = View.VISIBLE
            layoutNoBank.visibility = View.GONE

            val txtBankName: TextView = findViewById(R.id.txtBankName)
            val txtBankAccount: TextView = findViewById(R.id.txtBankAccount)
            val txtBankHolder: TextView = findViewById(R.id.txtBankHolder)

            txtBankName.text = "🏦 ${earningsStorage.getBankName(email)}"
            val acc = earningsStorage.getAccountNumber(email)
            txtBankAccount.text = "💳 ****${acc.takeLast(4)}"
            txtBankHolder.text = "👤 ${earningsStorage.getAccountHolderName(email)}"
        } else {
            layoutBankInfo.visibility = View.GONE
            layoutNoBank.visibility = View.VISIBLE
        }
    }

    private fun processWithdrawal() {
        val email = userStorage.getCurrentUserEmail()

        if (!earningsStorage.hasBankDetails(email)) {
            Toast.makeText(this, "❌ Add bank details first!", Toast.LENGTH_LONG).show()
            return
        }

        val amountStr = editAmount.text.toString().trim()
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toIntOrNull() ?: 0
        val balance = earningsStorage.getAvailableBalance(email)

        if (amount < 100) {
            Toast.makeText(this, "❌ Minimum withdrawal: ₹100", Toast.LENGTH_LONG).show()
            return
        }
        if (amount > balance) {
            Toast.makeText(this, "❌ Amount exceeds balance!", Toast.LENGTH_LONG).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("💸 Confirm Withdrawal")
            .setMessage(
                "Amount: ₹$amount\n\n" +
                        "To Bank:\n" +
                        "${earningsStorage.getBankName(email)}\n" +
                        "A/c: ****${earningsStorage.getAccountNumber(email).takeLast(4)}\n" +
                        "${earningsStorage.getAccountHolderName(email)}\n\n" +
                        "Money will be credited within 24 hours."
            )
            .setPositiveButton("✅ Confirm") { _, _ ->
                val withdrawalId = earningsStorage.withdrawMoney(
                    artisanEmail = email,
                    amount = amount,
                    bankName = earningsStorage.getBankName(email),
                    accountNumber = earningsStorage.getAccountNumber(email),
                    ifscCode = earningsStorage.getIfscCode(email),
                    accountHolderName = earningsStorage.getAccountHolderName(email)
                )

                if (withdrawalId != null) {
                    AlertDialog.Builder(this)
                        .setTitle("✅ Withdrawal Successful!")
                        .setMessage(
                            "ID: $withdrawalId\n\n" +
                                    "₹$amount transferred to your bank.\n\n" +
                                    "💳 ****${earningsStorage.getAccountNumber(email).takeLast(4)}\n\n" +
                                    "Money will reflect in your bank within 24 hours."
                        )
                        .setPositiveButton("OK") { _, _ ->
                            finish()
                            overridePendingTransition(0, 0)
                        }
                        .setCancelable(false)
                        .show()
                } else {
                    Toast.makeText(this, "❌ Withdrawal failed", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }
}