package com.example.nammapridechannapatna

data class Toy(
    val id: String,
    val name: String,
    val giNumber: String,
    val price: Int,
    val artisanName: String,
    val artisanBio: String,
    val materials: String,
    val description: String,
    val imageEmoji: String,
    val stock: Int = 0,
    val rating: Float = 0f,
    val photos: List<Int> = emptyList(),
    val isArtisanCreated: Boolean = false,    // ✅ NEW - Track if from artisan
    val artisanEmail: String = ""              // ✅ NEW - Owner email
)