package com.example.nammapridechannapatna

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class ArtisanStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ChannapatnaArtisan", Context.MODE_PRIVATE)

    // ================================
    // ✅ GENERATE UNIQUE GI ID
    // CN-YYYYMMDD-Random
    // ================================
    fun generateGIId(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val datePart = dateFormat.format(Date())
        val randomPart = Random.nextInt(1000, 9999)
        return "CN-$datePart-$randomPart"
    }

    // ================================
    // ✅ ADD NEW TOY (and sync to vendor view)
    // ================================
    fun addToy(
        name: String,
        photoIndex: Int,
        materials: String,
        craftingStory: String,
        price: Int,
        stock: Int,
        artisanEmail: String
    ): String {
        val giId = generateGIId()
        val createdDate = System.currentTimeMillis()

        val newToy = "$giId|$name|$photoIndex|$materials|$craftingStory|" +
                "$price|$stock|$artisanEmail|$createdDate;"

        val allToys = prefs.getString("artisan_toys", "") ?: ""
        prefs.edit().putString("artisan_toys", allToys + newToy).apply()

        // ✅ Force commit (immediate save)
        prefs.edit().commit()

        return giId
    }

    // ================================
    // ✅ GET ALL TOYS BY ARTISAN
    // ================================
    fun getMyToys(artisanEmail: String): List<ArtisanToy> {
        return getAllToys().filter { it.artisanEmail == artisanEmail }
    }

    // ================================
    // ✅ GET ALL TOYS (from all artisans)
    // ================================
    fun getAllToys(): List<ArtisanToy> {
        val data = prefs.getString("artisan_toys", "") ?: ""
        if (data.isEmpty()) return emptyList()

        return data.split(";").mapNotNull { entry ->
            val cleaned = entry.trim()
            if (cleaned.isEmpty()) return@mapNotNull null

            val parts = cleaned.split("|")
            if (parts.size >= 9) {
                ArtisanToy(
                    giId = parts[0],
                    name = parts[1],
                    photoIndex = parts[2].toIntOrNull() ?: 0,
                    materials = parts[3],
                    craftingStory = parts[4],
                    price = parts[5].toIntOrNull() ?: 0,
                    stock = parts[6].toIntOrNull() ?: 0,
                    artisanEmail = parts[7],
                    createdDate = parts[8].toLongOrNull() ?: 0L
                )
            } else null
        }.reversed()  // Latest first
    }

    // ================================
    // ✅ UPDATE STOCK (immediate save)
    // ================================
    fun updateStock(giId: String, newStock: Int): Boolean {
        val data = prefs.getString("artisan_toys", "") ?: ""
        if (data.isEmpty()) return false

        val toys = data.split(";").filter { it.isNotEmpty() }.toMutableList()
        var updated = false

        for (i in toys.indices) {
            val parts = toys[i].split("|")
            if (parts.size >= 9 && parts[0] == giId) {
                val list = parts.toMutableList()
                list[6] = newStock.coerceAtLeast(0).toString()
                toys[i] = list.joinToString("|")
                updated = true
                break
            }
        }

        if (updated) {
            // ✅ Use commit() instead of apply() for immediate save
            prefs.edit().putString("artisan_toys", toys.joinToString(";") + ";").commit()
        }
        return updated
    }

    // ================================
    // ✅ DELETE TOY
    // ================================
    fun deleteToy(giId: String): Boolean {
        val data = prefs.getString("artisan_toys", "") ?: ""
        if (data.isEmpty()) return false

        val toys = data.split(";").filter {
            it.isNotEmpty() && it.split("|").firstOrNull() != giId
        }

        prefs.edit().putString("artisan_toys", toys.joinToString(";") + ";").commit()
        return true
    }

    // ================================
    // ✅ GET TOY BY ID
    // ================================
    fun getToyByGI(giId: String): ArtisanToy? {
        return getAllToys().find { it.giId == giId }
    }

    // ================================
    // ✅ SAVE ARTISAN PROFILE
    // ================================
    fun saveArtisanProfile(
        email: String,
        name: String,
        experience: Int,
        workshopLocation: String,
        latitude: Double,
        longitude: Double,
        specialization: String,
        phone: String,
        bio: String
    ) {
        val profile = "$email|$name|$experience|$workshopLocation|" +
                "$latitude|$longitude|$specialization|$phone|$bio;"

        val allProfiles = prefs.getString("artisan_profiles", "") ?: ""
        val profiles = allProfiles.split(";").filter {
            it.isNotEmpty() && it.split("|").firstOrNull() != email
        }.toMutableList()

        profiles.add(profile.removeSuffix(";"))

        prefs.edit().putString("artisan_profiles", profiles.joinToString(";") + ";").commit()
    }

    // ================================
    // ✅ GET ARTISAN PROFILE
    // ================================
    fun getArtisanProfile(email: String): Artisan? {
        val allProfiles = prefs.getString("artisan_profiles", "") ?: ""
        if (allProfiles.isEmpty()) return null

        for (entry in allProfiles.split(";")) {
            if (entry.isEmpty()) continue
            val parts = entry.split("|")
            if (parts.size >= 9 && parts[0] == email) {
                return Artisan(
                    email = parts[0],
                    name = parts[1],
                    experience = parts[2].toIntOrNull() ?: 0,
                    workshopLocation = parts[3],
                    latitude = parts[4].toDoubleOrNull() ?: 0.0,
                    longitude = parts[5].toDoubleOrNull() ?: 0.0,
                    specialization = parts[6],
                    phone = parts[7],
                    bio = parts[8]
                )
            }
        }
        return null
    }

    // ================================
    // ✅ STATISTICS
    // ================================
    fun getTotalToys(artisanEmail: String): Int =
        getMyToys(artisanEmail).size

    fun getTotalStock(artisanEmail: String): Int =
        getMyToys(artisanEmail).sumOf { it.stock }

    fun getOutOfStockCount(artisanEmail: String): Int =
        getMyToys(artisanEmail).count { it.stock == 0 }

    fun getTotalArtisanToys(): Int = getAllToys().size
}