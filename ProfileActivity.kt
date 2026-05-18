package com.example.nammapridechannapatna

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class ProfileActivity : ComponentActivity() {

    private lateinit var userStorage: UserStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userStorage = UserStorage(this)

        val btnBack: TextView = findViewById(R.id.btnBack)
        val editName: EditText = findViewById(R.id.editProfileName)
        val editEmail: EditText = findViewById(R.id.editProfileEmail)
        val editDob: EditText = findViewById(R.id.editProfileDob)
        val editPhone: EditText = findViewById(R.id.editProfilePhone)
        val editAddress: EditText = findViewById(R.id.editProfileAddress)
        val btnSave: Button = findViewById(R.id.btnSaveProfile)

        // Load existing data
        editName.setText(userStorage.getCurrentUserName())
        editEmail.setText(userStorage.getCurrentUserEmail())
        editDob.setText(userStorage.getProfileDob())
        editPhone.setText(userStorage.getProfilePhone())
        editAddress.setText(userStorage.getProfileAddress())

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val name = editName.text.toString().trim()
            val dob = editDob.text.toString().trim()
            val phone = editPhone.text.toString().trim()
            val address = editAddress.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userStorage.updateUserName(name)
            userStorage.saveProfile(dob, address, phone)

            Toast.makeText(this, "✅ Profile saved successfully!", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}