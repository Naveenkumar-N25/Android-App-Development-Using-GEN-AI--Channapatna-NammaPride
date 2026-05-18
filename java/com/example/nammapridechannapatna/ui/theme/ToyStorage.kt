package com.example.nammapridechannapatna

import android.content.Context
import android.content.SharedPreferences

class ToyStorage(context: Context) {

    private val context: Context = context
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ChannapatnaToys", Context.MODE_PRIVATE)

    init {
        if (prefs.getBoolean("first_run", true)) {
            loadDefaultToys()
            prefs.edit().putBoolean("first_run", false).apply()
        }
    }

    // ================================
    // ✅ DEFAULT TOYS (Built-in)
    // ================================
    private fun loadDefaultToys() {
        val defaultToys = """
            T001|Wooden Spinning Top|GI-CHN-2006-001|150|Ravi Kumar|Master craftsman with 25 years of experience in Channapatna toy making. His family has been in the toy making business for 3 generations.|Rosewood, Lacquer, Natural Dyes|Traditional spinning top made from rosewood with vibrant lacquer finish. Each piece is hand-turned on a lathe and coated with natural lac.|🎯|5|4.5;
            T002|Rocking Horse|GI-CHN-2006-002|850|Lakshmi Devi|Award winning artisan specializing in traditional toys. She has won Karnataka State Award for handicrafts.|Ivory wood, Vegetable colors, Cotton thread, Metal pins|Beautiful rocking horse painted with non-toxic vegetable colors. A classic toy that brings joy to every child.|🐴|3|4.8;
            T003|Wooden Doll Set|GI-CHN-2006-003|650|Manjunath B|Third generation toy maker from Channapatna. He specializes in traditional doll making.|Hale wood, Lac colors, Natural polish, Beeswax|Set of 5 colorful wooden dolls representing Indian culture. Each doll is hand-painted with intricate designs.|🪆|8|4.3;
            T004|Stacking Rings|GI-CHN-2006-004|350|Suma R|Experienced female artisan promoting eco-friendly toys. She trains young women in toy making.|Rosewood, Lac dye, Beeswax, Natural finish|Educational stacking rings toy for toddlers. Helps develop motor skills and color recognition.|🎨|12|4.6;
            T005|Pull Along Elephant|GI-CHN-2006-005|750|Krishna Murthy|Veteran craftsman known for animal designs with 30 years experience. His elephant toys are famous.|Ivory wood, Eco lacquer, Cotton thread, Wooden wheels|Charming elephant toy with wheels and pull string. Hand carved with attention to detail.|🐘|4|4.9;
            T006|Wooden Train Set|GI-CHN-2006-006|1200|Venkatesh K|Innovative artisan creating modern designs while keeping traditional techniques. Won national award in 2019.|Rosewood, Mixed colors, Wooden wheels, Metal axles, Cotton rope|Colorful train set with engine and three coaches. Each coach has different color and can be detached.|🚂|2|4.7
        """.trimIndent()

        prefs.edit().putString("all_toys", defaultToys).apply()
    }

    // ================================
    // ✅ GET ALL TOYS - Built-in + Artisan
    // ================================
    fun getAllToys(): List<Toy> {
        // Get built-in toys
        val data = prefs.getString("all_toys", "") ?: ""
        val builtInToys = parseToys(data)

        // ✅ Get artisan-created toys
        val artisanToys = getArtisanCreatedToys()

        // Combine both lists (artisan toys at top - newest first)
        return artisanToys + builtInToys
    }

    // ================================
    // ✅ GET ARTISAN-CREATED TOYS
    // ================================
    private fun getArtisanCreatedToys(): List<Toy> {
        val artisanStorage = ArtisanStorage(context)
        val allArtisanToys = artisanStorage.getAllToys()

        val photoList = listOf(
            R.drawable.toy_image_1,
            R.drawable.toy_image_2,
            R.drawable.toy_image_3,
            R.drawable.toy_image_4,
            R.drawable.toy_image_5,
            R.drawable.toy_image_6
        )

        return allArtisanToys.map { artisanToy ->
            // Get artisan profile for bio
            val artisanProfile = artisanStorage.getArtisanProfile(artisanToy.artisanEmail)
            val artisanName = artisanProfile?.name ?: "Channapatna Artisan"
            val artisanBio = artisanProfile?.bio ?:
            "Skilled craftsman from Channapatna with traditional knowledge"

            Toy(
                id = artisanToy.giId,
                name = artisanToy.name,
                giNumber = artisanToy.giId,
                price = artisanToy.price,
                artisanName = artisanName,
                artisanBio = artisanBio,
                materials = artisanToy.materials,
                description = artisanToy.craftingStory,
                imageEmoji = "🎨",
                stock = artisanToy.stock,
                rating = 4.5f,
                photos = photoList,
                isArtisanCreated = true,
                artisanEmail = artisanToy.artisanEmail
            )
        }
    }

    // ================================
    // ✅ SEARCH TOYS
    // ================================
    fun searchToys(query: String): List<Toy> {
        if (query.isEmpty()) return getAllToys()
        return getAllToys().filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.giNumber.contains(query, ignoreCase = true) ||
                    it.artisanName.contains(query, ignoreCase = true)
        }
    }

    // ================================
    // ✅ GET TOY BY ID
    // ================================
    fun getToyById(id: String): Toy? = getAllToys().find { it.id == id }

    // ================================
    // ✅ PARSE BUILT-IN TOYS
    // ================================
    private fun parseToys(data: String): List<Toy> {
        if (data.isEmpty()) return emptyList()

        val photoList = listOf(
            R.drawable.toy_image_1,
            R.drawable.toy_image_2,
            R.drawable.toy_image_3,
            R.drawable.toy_image_4,
            R.drawable.toy_image_5,
            R.drawable.toy_image_6
        )

        return data.split(";").mapNotNull { entry ->
            val cleaned = entry.trim()
            if (cleaned.isEmpty()) return@mapNotNull null

            val parts = cleaned.split("|")
            if (parts.size >= 9) {
                Toy(
                    id = parts[0],
                    name = parts[1],
                    giNumber = parts[2],
                    price = parts[3].toIntOrNull() ?: 0,
                    artisanName = parts[4],
                    artisanBio = parts[5],
                    materials = parts[6],
                    description = parts[7],
                    imageEmoji = parts[8],
                    stock = parts.getOrNull(9)?.toIntOrNull() ?: 0,
                    rating = parts.getOrNull(10)?.toFloatOrNull() ?: 0f,
                    photos = photoList,
                    isArtisanCreated = false,
                    artisanEmail = ""
                )
            } else null
        }
    }

    // ================================
    // ✅ ADD TO CART
    // ================================
    fun addToCart(toyId: String): Boolean {
        val cart = prefs.getString("cart_list", "") ?: ""
        val cartItems = cart.split(",").filter { it.isNotEmpty() }
        if (cartItems.contains(toyId)) return false

        val newCart = if (cart.isEmpty()) toyId else "$cart,$toyId"
        prefs.edit().putString("cart_list", newCart).apply()
        return true
    }

    // ================================
    // ✅ REMOVE FROM CART
    // ================================
    fun removeFromCart(toyId: String) {
        val cart = prefs.getString("cart_list", "") ?: ""
        val cartItems = cart.split(",").filter { it.isNotEmpty() && it != toyId }
        prefs.edit().putString("cart_list", cartItems.joinToString(",")).apply()
    }

    // ================================
    // ✅ GET CART ITEMS
    // ================================
    fun getCartItems(): List<Toy> {
        val cart = prefs.getString("cart_list", "") ?: ""
        if (cart.isEmpty()) return emptyList()
        return cart.split(",").filter { it.isNotEmpty() }.mapNotNull { getToyById(it) }
    }

    // ================================
    // ✅ GET CART COUNT
    // ================================
    fun getCartCount(): Int {
        val cart = prefs.getString("cart_list", "") ?: ""
        if (cart.isEmpty()) return 0
        return cart.split(",").filter { it.isNotEmpty() }.size
    }

    // ================================
    // ✅ CLEAR CART
    // ================================
    fun clearCart() {
        prefs.edit().putString("cart_list", "").apply()
    }

    // ================================
    // ✅ IS IN CART
    // ================================
    fun isInCart(toyId: String): Boolean {
        val cart = prefs.getString("cart_list", "") ?: ""
        return cart.split(",").contains(toyId)
    }

    // ================================
    // ✅ GET TOTAL TOY COUNT
    // ================================
    fun getTotalToyCount(): Int = getAllToys().size

    // ================================
    // ✅ GET ARTISAN TOYS COUNT
    // ================================
    fun getArtisanToyCount(): Int = getArtisanCreatedToys().size
}