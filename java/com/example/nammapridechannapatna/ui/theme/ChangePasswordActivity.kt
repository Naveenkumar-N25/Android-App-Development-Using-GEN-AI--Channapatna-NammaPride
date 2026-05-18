package com.example.nammapridechannapatna

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class ChangePasswordActivity : ComponentActivity() {

    private lateinit var userStorage: UserStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        userStorage = UserStorage(this)

        val btnBack: TextView = findViewById(R.id.btnBack)
        val editCurrent: EditText = findViewById(R.id.editCurrentPassword)
        val editNew: EditText = findViewById(R.id.editNewPassword)
        val editConfirm: EditText = findViewById(R.id.editConfirmPassword)
        val btnChange: Button = findViewById(R.id.btnChangePassword)

        btnBack.setOnClickListener { finish() }

        btnChange.setOnClickListener {
            val currentPass = editCurrent.text.toString().trim()
            val newPass = editNew.text.toString().trim()
            val confirmPass = editConfirm.text.toString().trim()

            val errors = mutableListOf<String>()

            if (currentPass.isEmpty()) errors.add("• Enter current password")
            if (newPass.isEmpty()) errors.add("• Enter new password")
            if (confirmPass.isEmpty()) errors.add("• Confirm new password")

            if (newPass != confirmPass) errors.add("• Passwords do not match")

            if (newPass.isNotEmpty()) {
                errors.addAll(userStorage.validatePassword(newPass))
            }

            if (errors.isNotEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(errors.joinToString("\n"))
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            // Verify current password
            val email = userStorage.getCurrentUserEmail()
            val loginResult = userStorage.loginUser(email, currentPass)

            if (loginResult == null) {
                Toast.makeText(this, "❌ Current password is wrong!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val success = userStorage.resetPassword(email, newPass)

            if (success) {
                Toast.makeText(this, "✅ Password changed!", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "❌ Failed to change", Toast.LENGTH_LONG).show()
            }
        }
    }
}