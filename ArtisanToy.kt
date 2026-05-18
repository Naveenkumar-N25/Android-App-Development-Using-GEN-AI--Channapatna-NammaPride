package com.example.nammapridechannapatna

data class ArtisanToy(
    val giId: String,              // CN-YYYYMMDD-Random
    val name: String,
    val photoIndex: Int,           // 0-5 photo selection
    val materials: String,         // Aale Mara wood, vegetable dyes etc
    val craftingStory: String,     // Heritage process
    val price: Int,
    val stock: Int,
    val artisanEmail: String,      // Owner of this toy
    val createdDate: Long
)