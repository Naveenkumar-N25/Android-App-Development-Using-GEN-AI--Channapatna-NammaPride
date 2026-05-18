package com.example.nammapridechannapatna

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : ComponentActivity() {

    private lateinit var toyStorage: ToyStorage
    private lateinit var userStorage: UserStorage
    private lateinit var orderStorage: OrderStorage
    private lateinit var cartAdapter: CartAdapter
    private lateinit var recyclerCart: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var layoutTotal: LinearLayout
    private lateinit var txtTotalAmount: TextView
    private lateinit var txtCartCount: TextView
    private lateinit var btnBack: TextView
    private lateinit var btnCheckout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toyStorage = ToyStorage(this)
        userStorage = UserStorage(this)
        orderStorage = OrderStorage(this)

        recyclerCart = findViewById(R.id.recyclerCart)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        layoutTotal = findViewById(R.id.layoutTotal)
        txtTotalAmount = findViewById(R.id.txtTotalAmount)
        txtCartCount = findViewById(R.id.txtCartCount)
        btnBack = findViewById(R.id.btnBack)
        btnCheckout = findViewById(R.id.btnCheckout)

        setupRecyclerView()
        loadCart()

        btnBack.setOnClickListener { finish() }
        btnCheckout.setOnClickListener { placeOrder() }
    }

    override fun onResume() {
        super.onResume()
        loadCart()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            toyStorage.getCartItems(),
            onRemove = { toy ->
                toyStorage.removeFromCart(toy.id)
                Toast.makeText(this, "❌ Removed ${toy.name}", Toast.LENGTH_SHORT).show()
                loadCart()
            }
        )
        recyclerCart.layoutManager = LinearLayoutManager(this)
        recyclerCart.adapter = cartAdapter
    }

    private fun loadCart() {
        val items = toyStorage.getCartItems()
        cartAdapter.updateList(items)

        txtCartCount.text = "${items.size} items"
        val total = items.sumOf { it.price }
        txtTotalAmount.text = "₹$total"

        if (items.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            recyclerCart.visibility = View.GONE
            layoutTotal.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            recyclerCart.visibility = View.VISIBLE
            layoutTotal.visibility = View.VISIBLE
        }
    }

    // ================================
    // ✅ Place Order
    // ================================
    private fun placeOrder() {
        val items = toyStorage.getCartItems()
        if (items.isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val address = userStorage.getProfileAddress()
        val phone = userStorage.getProfilePhone()
        val customerName = userStorage.getCurrentUserName()

        if (address.isEmpty() || phone.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("⚠️ Profile Incomplete")
                .setMessage("Please add your address and phone number in Profile Settings before placing order.")
                .setPositiveButton("Go to Profile") { _, _ ->
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                .setNegativeButton("Cancel", null)
                .show()
            return
        }

        val total = items.sumOf { it.price }
        val itemNames = items.joinToString(", ") { it.name }
        val itemIds = items.joinToString(",") { it.id }

        AlertDialog.Builder(this)
            .setTitle("📦 Confirm Order")
            .setMessage(
                "Items: $itemNames\n\n" +
                        "Total: ₹$total\n" +
                        "Shipping: FREE\n" +
                        "Payment: Cash on Delivery\n\n" +
                        "Delivery To:\n$customerName\n$phone\n$address"
            )
            .setPositiveButton("✅ Place Order") { _, _ ->
                // ✅ Save order to OrderStorage
                val orderId = orderStorage.placeOrder(
                    items = itemIds,
                    itemNames = itemNames,
                    totalAmount = total,
                    deliveryAddress = address,
                    customerName = customerName,
                    customerPhone = phone
                )

                // ✅ Clear cart
                toyStorage.clearCart()
                loadCart()

                // ✅ Show success and go to Orders
                AlertDialog.Builder(this)
                    .setTitle("✅ Order Placed!")
                    .setMessage(
                        "Order ID: $orderId\n\n" +
                                "Your order has been confirmed.\n" +
                                "You can track your order in My Orders section."
                    )
                    .setPositiveButton("View Orders") { _, _ ->
                        val intent = Intent(this, OrderActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton("Continue Shopping") { _, _ ->
                        finish()
                    }
                    .setCancelable(false)
                    .show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}