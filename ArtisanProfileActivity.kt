package com.example.nammapridechannapatna

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class ArtisanProfileActivity : ComponentActivity() {

    private lateinit var artisanStorage: ArtisanStorage
    private lateinit var userStorage: UserStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artisan_profile)
        overridePendingTransition(0, 0)

        artisanStorage = ArtisanStorage(this)
        userStorage = UserStorage(this)

        val btnBack: TextView = findViewById(R.id.btnBack)
        val editName: EditText = findViewById(R.id.editArtisanName)
        val editEmail: EditText = findViewById(R.id.editArtisanEmail)
        val editPhone: EditText = findViewById(R.id.editArtisanPhone)
        val editSpec: EditText = findViewById(R.id.editSpecialization)
        val editExp: EditText = findViewById(R.id.editExperience)
        val editAddress: EditText = findViewById(R.id.editWorkshopAddress)
        val editLat: EditText = findViewById(R.id.editLatitude)
        val editLng: EditText = findViewById(R.id.editLongitude)
        val editBio: EditText = findViewById(R.id.editArtisanBio)
        val btnUseChannapatna: Button = findViewById(R.id.btnUseChannapatna)
        val btnSave: Button = findViewById(R.id.btnSaveProfile)

        // Load existing data
        val email = userStorage.getCurrentUserEmail()
        editEmail.setText(email)
        editName.setText(userStorage.getCurrentUserName())

        val profile = artisanStorage.getArtisanProfile(email)
        if (profile != null) {
            editPhone.setText(profile.phone)
            editSpec.setText(profile.specialization)
            editExp.setText(profile.experience.toString())
            editAddress.setText(profile.workshopLocation)
            editLat.setText(profile.latitude.toString())
            editLng.setText(profile.longitude.toString())
            editBio.setText(profile.bio)
        }

        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        // Channapatna default coordinates
        btnUseChannapatna.setOnClickListener {
            editLat.setText("12.6516")
            editLng.setText("77.2058")
            Toast.makeText(this, "📍 Channapatna location set", Toast.LENGTH_SHORT).show()
        }

        btnSave.setOnClickListener {
            val name = editName.text.toString().trim()
            val phone = editPhone.text.toString().trim()
            val spec = editSpec.text.toString().trim()
            val expStr = editExp.text.toString().trim()
            val address = editAddress.text.toString().trim()
            val latStr = editLat.text.toString().trim()
            val lngStr = editLng.text.toString().trim()
            val bio = editBio.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val experience = expStr.toIntOrNull() ?: 0
            val latitude = latStr.toDoubleOrNull() ?: 0.0
            val longitude = lngStr.toDoubleOrNull() ?: 0.0

            userStorage.updateUserName(name)
            artisanStorage.saveArtisanProfile(
                email = email,
                name = name,
                experience = experience,
                workshopLocation = address,
                latitude = latitude,
                longitude = longitude,
                specialization = spec,
                phone = phone,
                bio = bio
            )

            Toast.makeText(this, "✅ Profile saved!", Toast.LENGTH_LONG).show()
            finish()
            overridePendingTransition(0, 0)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }
}