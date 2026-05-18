package com.example.nammapridechannapatna

data class Artisan(
    val email: String,
    val name: String,
    val experience: Int,           // Years of experience
    val workshopLocation: String,  // Address
    val latitude: Double,          // GPS coordinates
    val longitude: Double,
    val specialization: String,    // Type of toys made
    val phone: String,
    val bio: String
)