package com.example.nammapridechannapatna

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class ToyDetailActivity : ComponentActivity() {

    private lateinit var toyStorage: ToyStorage
    private lateinit var userStorage: UserStorage
    private lateinit var orderStorage: OrderStorage
    private lateinit var imgMainPhoto: ImageView
    private lateinit var txtPhotoCounter: TextView
    private lateinit var txtEmojiOverlay: TextView
    private var currentPhotoIndex = 0
    private lateinit var currentToy: Toy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toy_detail)

        toyStorage = ToyStorage(this)
        userStorage = UserStorage(this)
        orderStorage = OrderStorage(this)

        val toyId = intent.getStringExtra("TOY_ID") ?: ""
        val toy = toyStorage.getToyById(toyId)

        if (toy == null) {
            Toast.makeText(this, "Toy not found!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentToy = toy

        val btnBack: TextView = findViewById(R.id.btnBack)
        imgMainPhoto = findViewById(R.id.imgMainPhoto)
        txtPhotoCounter = findViewById(R.id.txtPhotoCounter)
        txtEmojiOverlay = findViewById(R.id.txtEmojiOverlay)
        val layoutThumbnails: LinearLayout = findViewById(R.id.layoutThumbnails)
        val txtDetailName: TextView = findViewById(R.id.txtDetailName)
        val txtRating: TextView = findViewById(R.id.txtRating)
        val txtStock: TextView = findViewById(R.id.txtStock)
        val txtDetailGINumber: TextView = findViewById(R.id.txtDetailGINumber)
        val txtDetailPrice: TextView = findViewById(R.id.txtDetailPrice)
        val txtDetailDescription: TextView = findViewById(R.id.txtDetailDescription)
        val txtDetailArtisan: TextView = findViewById(R.id.txtDetailArtisan)
        val txtDetailArtisanBio: TextView = findViewById(R.id.txtDetailArtisanBio)
        val txtDetailMaterials: TextView = findViewById(R.id.txtDetailMaterials)
        val btnDetailAddToCart: Button = findViewById(R.id.btnDetailAddToCart)
        val btnBuyNow: Button = findViewById(R.id.btnBuyNow)

        txtEmojiOverlay.text = toy.imageEmoji
        txtDetailName.text = toy.name
        txtRating.text = "⭐ ${toy.rating}"
        txtStock.text = if (toy.stock > 0) "In Stock: ${toy.stock}" else "Out of Stock"
        txtStock.setTextColor(if (toy.stock > 0) Color.parseColor("#2E7D32") else Color.RED)
        txtDetailGINumber.text = "GI: ${toy.giNumber}"
        txtDetailPrice.text = "₹${toy.price}"
        txtDetailDescription.text = toy.description
        txtDetailArtisan.text = toy.artisanName
        txtDetailArtisanBio.text = toy.artisanBio
        txtDetailMaterials.text = toy.materials.split(",").joinToString("\n") { "• ${it.trim()}" }

        if (toy.photos.isNotEmpty()) {
            imgMainPhoto.setImageResource(toy.photos[0])
            txtPhotoCounter.text = "1 / ${toy.photos.size}"
        }

        buildThumbnails(toy, layoutThumbnails)

        btnBack.setOnClickListener { finish() }

        btnDetailAddToCart.setOnClickListener {
            val success = toyStorage.addToCart(toy.id)
            if (success) {
                Toast.makeText(this, "✅ Added to cart!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "⚠️ Already in cart", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ Buy Now - Direct Order without going to cart
        btnBuyNow.setOnClickListener { buyNow() }
    }

    // ================================
    // ✅ Buy Now - Direct to Order
    // ================================
    private fun buyNow() {
        val address = userStorage.getProfileAddress()
        val phone = userStorage.getProfilePhone()
        val customerName = userStorage.getCurrentUserName()

        if (address.isEmpty() || phone.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("⚠️ Profile Incomplete")
                .setMessage("Please add address and phone in Profile Settings first.")
                .setPositiveButton("Go to Profile") { _, _ ->
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                .setNegativeButton("Cancel", null)
                .show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("⚡ Quick Buy")
            .setMessage(
                "Item: ${currentToy.name}\n" +
                        "Price: ₹${currentToy.price}\n\n" +
                        "Shipping: FREE\n" +
                        "Payment: Cash on Delivery\n\n" +
                        "Deliver To:\n$customerName\n$phone\n$address"
            )
            .setPositiveButton("✅ Confirm Order") { _, _ ->
                // ✅ Save order
                val orderId = orderStorage.placeOrder(
                    items = currentToy.id,
                    itemNames = currentToy.name,
                    totalAmount = currentToy.price,
                    deliveryAddress = address,
                    customerName = customerName,
                    customerPhone = phone
                )

                // ✅ Show success and go to Orders
                AlertDialog.Builder(this)
                    .setTitle("✅ Order Placed!")
                    .setMessage(
                        "Order ID: $orderId\n\n" +
                                "Your ${currentToy.name} has been ordered.\n" +
                                "Track your order in My Orders section."
                    )
                    .setPositiveButton("Track Order") { _, _ ->
                        val intent = Intent(this, OrderTrackingActivity::class.java)
                        intent.putExtra("ORDER_ID", orderId)
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton("View All Orders") { _, _ ->
                        startActivity(Intent(this, OrderActivity::class.java))
                        finish()
                    }
                    .setCancelable(false)
                    .show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun buildThumbnails(toy: Toy, container: LinearLayout) {
        container.removeAllViews()

        for (i in toy.photos.indices) {
            val thumbnailFrame = LinearLayout(this)
            thumbnailFrame.orientation = LinearLayout.VERTICAL
            thumbnailFrame.setPadding(6, 6, 6, 6)

            val imgThumbnail = ImageView(this)
            imgThumbnail.setImageResource(toy.photos[i])
            imgThumbnail.scaleType = ImageView.ScaleType.CENTER_CROP

            val params = LinearLayout.LayoutParams(180, 180)
            params.setMargins(8, 0, 8, 0)
            imgThumbnail.layoutParams = params

            if (i == currentPhotoIndex) {
                imgThumbnail.setBackgroundColor(Color.parseColor("#8D4E2A"))
                imgThumbnail.setPadding(6, 6, 6, 6)
            } else {
                imgThumbnail.setBackgroundColor(Color.parseColor("#FFE0B2"))
                imgThumbnail.setPadding(3, 3, 3, 3)
            }

            imgThumbnail.setOnClickListener {
                currentPhotoIndex = i
                imgMainPhoto.setImageResource(toy.photos[i])
                txtPhotoCounter.text = "${i + 1} / ${toy.photos.size}"
                buildThumbnails(toy, container)
            }

            thumbnailFrame.addView(imgThumbnail)

            val labelView = TextView(this)
            labelView.text = "Photo ${i + 1}"
            labelView.textSize = 10f
            labelView.setTextColor(Color.parseColor("#5D4037"))
            labelView.gravity = android.view.Gravity.CENTER
            labelView.setPadding(0, 4, 0, 0)

            thumbnailFrame.addView(labelView)
            container.addView(thumbnailFrame)
        }
    }
}