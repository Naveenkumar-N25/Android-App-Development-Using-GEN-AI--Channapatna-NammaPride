package com.example.nammapridechannapatna

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class AddToyActivity : ComponentActivity() {

    private lateinit var artisanStorage: ArtisanStorage
    private lateinit var userStorage: UserStorage
    private var selectedPhotoIndex = 0
    private val photoResources = listOf(
        R.drawable.toy_image_1,
        R.drawable.toy_image_2,
        R.drawable.toy_image_3,
        R.drawable.toy_image_4,
        R.drawable.toy_image_5,
        R.drawable.toy_image_6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_toy)
        overridePendingTransition(0, 0)

        artisanStorage = ArtisanStorage(this)
        userStorage = UserStorage(this)

        val btnBack: TextView = findViewById(R.id.btnBack)
        val layoutPhotoSelection: LinearLayout = findViewById(R.id.layoutPhotoSelection)
        val txtSelectedPhoto: TextView = findViewById(R.id.txtSelectedPhoto)
        val editToyName: EditText = findViewById(R.id.editToyName)
        val txtGeneratedGI: TextView = findViewById(R.id.txtGeneratedGI)
        val editMaterials: EditText = findViewById(R.id.editMaterials)
        val editCraftingStory: EditText = findViewById(R.id.editCraftingStory)
        val editPrice: EditText = findViewById(R.id.editPrice)
        val editStock: EditText = findViewById(R.id.editStock)
        val btnSaveToy: Button = findViewById(R.id.btnSaveToy)

        // Generate GI ID instantly
        val giId = artisanStorage.generateGIId()
        txtGeneratedGI.text = giId

        // Build photo selection
        buildPhotoSelection(layoutPhotoSelection, txtSelectedPhoto)

        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        // ✅ Save Toy
        btnSaveToy.setOnClickListener {
            val name = editToyName.text.toString().trim()
            val materials = editMaterials.text.toString().trim()
            val story = editCraftingStory.text.toString().trim()
            val priceStr = editPrice.text.toString().trim()
            val stockStr = editStock.text.toString().trim()

            val errors = mutableListOf<String>()
            if (name.isEmpty()) errors.add("• Toy name required")
            if (materials.isEmpty()) errors.add("• Materials required")
            if (story.isEmpty()) errors.add("• Crafting story required")
            if (priceStr.isEmpty()) errors.add("• Price required")
            if (stockStr.isEmpty()) errors.add("• Stock required")

            val price = priceStr.toIntOrNull() ?: 0
            val stock = stockStr.toIntOrNull() ?: 0

            if (price <= 0) errors.add("• Price must be greater than 0")
            if (stock < 0) errors.add("• Stock cannot be negative")

            if (errors.isNotEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("⚠️ Validation Failed")
                    .setMessage(errors.joinToString("\n"))
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            val email = userStorage.getCurrentUserEmail()
            val savedGI = artisanStorage.addToy(
                name = name,
                photoIndex = selectedPhotoIndex,
                materials = materials,
                craftingStory = story,
                price = price,
                stock = stock,
                artisanEmail = email
            )

            // ✅ Show success message - confirms it's available to vendors
            AlertDialog.Builder(this)
                .setTitle("✅ Toy Added Successfully!")
                .setMessage(
                    "🪆 Toy: $name\n" +
                            "✓ GI ID: $savedGI\n" +
                            "💰 Price: ₹$price\n" +
                            "📦 Stock: $stock pieces\n\n" +
                            "🛒 Your toy is now LIVE!\n" +
                            "Vendors can see and purchase it.\n\n" +
                            "✨ It will appear in vendor dashboard."
                )
                .setPositiveButton("View My Toys") { _, _ ->
                    finish()
                    overridePendingTransition(0, 0)
                }
                .setNegativeButton("Add Another") { _, _ ->
                    // Reset form
                    editToyName.setText("")
                    editMaterials.setText("")
                    editCraftingStory.setText("")
                    editPrice.setText("")
                    editStock.setText("")
                    txtGeneratedGI.text = artisanStorage.generateGIId()
                    selectedPhotoIndex = 0
                    buildPhotoSelection(layoutPhotoSelection, txtSelectedPhoto)
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun buildPhotoSelection(container: LinearLayout, txtSelected: TextView) {
        container.removeAllViews()

        for (i in photoResources.indices) {
            val photoFrame = LinearLayout(this)
            photoFrame.orientation = LinearLayout.VERTICAL
            photoFrame.setPadding(6, 6, 6, 6)

            val imgPhoto = ImageView(this)
            imgPhoto.setImageResource(photoResources[i])
            imgPhoto.scaleType = ImageView.ScaleType.CENTER_CROP

            val params = LinearLayout.LayoutParams(180, 180)
            params.setMargins(8, 0, 8, 0)
            imgPhoto.layoutParams = params

            if (i == selectedPhotoIndex) {
                imgPhoto.setBackgroundColor(Color.parseColor("#2E7D32"))
                imgPhoto.setPadding(8, 8, 8, 8)
            } else {
                imgPhoto.setBackgroundColor(Color.parseColor("#FFE0B2"))
                imgPhoto.setPadding(3, 3, 3, 3)
            }

            imgPhoto.setOnClickListener {
                selectedPhotoIndex = i
                txtSelected.text = "✓ Photo ${i + 1} selected"
                buildPhotoSelection(container, txtSelected)
            }

            val label = TextView(this)
            label.text = "Photo ${i + 1}"
            label.textSize = 10f
            label.setTextColor(Color.parseColor("#5D4037"))
            label.gravity = android.view.Gravity.CENTER
            label.setPadding(0, 4, 0, 0)

            photoFrame.addView(imgPhoto)
            photoFrame.addView(label)
            container.addView(photoFrame)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }
}