package com.example.nammapridechannapatna

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class BankDetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_details)
        overridePendingTransition(0, 0)

        val earningsStorage = EarningsStorage(this)
        val userStorage = UserStorage(this)
        val email = userStorage.getCurrentUserEmail()

        val btnBack: TextView = findViewById(R.id.btnBack)
        val editBankName: EditText = findViewById(R.id.editBankName)
        val editHolderName: EditText = findViewById(R.id.editHolderName)
        val editAccountNumber: EditText = findViewById(R.id.editAccountNumber)
        val editIfsc: EditText = findViewById(R.id.editIfsc)
        val editUpi: EditText = findViewById(R.id.editUpi)
        val btnSave: Button = findViewById(R.id.btnSaveBank)

        // Load existing data
        editBankName.setText(earningsStorage.getBankName(email))
        editHolderName.setText(earningsStorage.getAccountHolderName(email))
        editAccountNumber.setText(earningsStorage.getAccountNumber(email))
        editIfsc.setText(earningsStorage.getIfscCode(email))
        editUpi.setText(earningsStorage.getUpiId(email))

        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        btnSave.setOnClickListener {
            val bankName = editBankName.text.toString().trim()
            val holderName = editHolderName.text.toString().trim()
            val accountNumber = editAccountNumber.text.toString().trim()
            val ifsc = editIfsc.text.toString().trim().uppercase()
            val upi = editUpi.text.toString().trim()

            if (bankName.isEmpty()) {
                Toast.makeText(this, "Bank name required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (holderName.isEmpty()) {
                Toast.makeText(this, "Account holder name required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (accountNumber.length < 9) {
                Toast.makeText(this, "Invalid account number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ifsc.length != 11) {
                Toast.makeText(this, "IFSC must be 11 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            earningsStorage.saveBankDetails(
                artisanEmail = email,
                bankName = bankName,
                accountNumber = accountNumber,
                ifscCode = ifsc,
                accountHolderName = holderName,
                upiId = upi
            )

            Toast.makeText(this, "✅ Bank details saved!", Toast.LENGTH_LONG).show()
            finish()
            overridePendingTransition(0, 0)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }
}