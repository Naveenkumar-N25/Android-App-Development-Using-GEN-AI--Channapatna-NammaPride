package com.example.nammapridechannapatna

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderTrackingActivity : ComponentActivity() {

    private lateinit var orderStorage: OrderStorage
    private lateinit var currentOrder: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_tracking)

        orderStorage = OrderStorage(this)

        val orderId = intent.getStringExtra("ORDER_ID") ?: ""
        val order = orderStorage.getOrderById(orderId)

        if (order == null) {
            Toast.makeText(this, "Order not found!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentOrder = order

        val btnBack: TextView = findViewById(R.id.btnBack)
        val btnCancelOrder: Button = findViewById(R.id.btnCancelOrder)

        btnBack.setOnClickListener { finish() }

        // Display order info
        displayOrderInfo()

        // Update tracking timeline
        updateTimeline()

        // Cancel button
        val currentStatus = orderStorage.getCurrentStatus(order)
        if (currentStatus == "Delivered" || currentStatus == "Cancelled" ||
            currentStatus == "OutForDelivery") {
            btnCancelOrder.visibility = View.GONE
        } else {
            btnCancelOrder.setOnClickListener { confirmCancelOrder() }
        }
    }

    // ================================
    // ✅ Display Order Info
    // ================================
    private fun displayOrderInfo() {
        val txtOrderId: TextView = findViewById(R.id.txtTrackOrderId)
        val txtOrderDate: TextView = findViewById(R.id.txtTrackOrderDate)
        val txtItems: TextView = findViewById(R.id.txtTrackItems)
        val txtTotal: TextView = findViewById(R.id.txtTrackTotal)
        val txtCustomerName: TextView = findViewById(R.id.txtTrackCustomerName)
        val txtPhone: TextView = findViewById(R.id.txtTrackPhone)
        val txtAddress: TextView = findViewById(R.id.txtTrackAddress)

        txtOrderId.text = "📦 ${currentOrder.orderId}"

        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        txtOrderDate.text = "Ordered on: ${dateFormat.format(Date(currentOrder.orderDate))}"

        txtItems.text = currentOrder.itemNames
        txtTotal.text = "₹${currentOrder.totalAmount}"
        txtCustomerName.text = "👤 ${currentOrder.customerName}"
        txtPhone.text = "📞 ${currentOrder.customerPhone}"
        txtAddress.text = "📍 ${currentOrder.deliveryAddress}"
    }

    // ================================
    // ✅ Update Timeline based on Status
    // ================================
    private fun updateTimeline() {
        val currentStatus = orderStorage.getCurrentStatus(currentOrder)
        val orderDate = currentOrder.orderDate

        val iconStep1: TextView = findViewById(R.id.iconStep1)
        val iconStep2: TextView = findViewById(R.id.iconStep2)
        val iconStep3: TextView = findViewById(R.id.iconStep3)
        val iconStep4: TextView = findViewById(R.id.iconStep4)
        val iconStep5: TextView = findViewById(R.id.iconStep5)

        val txtStep1Date: TextView = findViewById(R.id.txtStep1Date)
        val txtStep2Date: TextView = findViewById(R.id.txtStep2Date)
        val txtStep3Date: TextView = findViewById(R.id.txtStep3Date)
        val txtStep4Date: TextView = findViewById(R.id.txtStep4Date)
        val txtStep5Date: TextView = findViewById(R.id.txtStep5Date)

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        // Step 1: Always Confirmed (Active)
        markActive(iconStep1, "✓")
        txtStep1Date.text = dateFormat.format(Date(orderDate))

        // Step 2: Packed (Day +1)
        if (currentStatus in listOf("Packed", "Shipped", "OutForDelivery", "Delivered")) {
            markActive(iconStep2, "✓")
            txtStep2Date.text = dateFormat.format(Date(orderDate + 24 * 60 * 60 * 1000))
        } else {
            txtStep2Date.text = "Expected: ${dateFormat.format(Date(orderDate + 24 * 60 * 60 * 1000))}"
        }

        // Step 3: Shipped (Day +2)
        if (currentStatus in listOf("Shipped", "OutForDelivery", "Delivered")) {
            markActive(iconStep3, "✓")
            txtStep3Date.text = dateFormat.format(Date(orderDate + 2 * 24 * 60 * 60 * 1000))
        } else {
            txtStep3Date.text = "Expected: ${dateFormat.format(Date(orderDate + 2 * 24 * 60 * 60 * 1000))}"
        }

        // Step 4: Out for Delivery (Day +4)
        if (currentStatus in listOf("OutForDelivery", "Delivered")) {
            markActive(iconStep4, "✓")
            txtStep4Date.text = dateFormat.format(Date(orderDate + 4 * 24 * 60 * 60 * 1000))
        } else {
            txtStep4Date.text = "Expected: ${dateFormat.format(Date(orderDate + 4 * 24 * 60 * 60 * 1000))}"
        }

        // Step 5: Delivered (Day +5)
        if (currentStatus == "Delivered") {
            markActive(iconStep5, "✓")
            txtStep5Date.text = dateFormat.format(Date(currentOrder.expectedDelivery))
        } else {
            txtStep5Date.text = "Expected: ${dateFormat.format(Date(currentOrder.expectedDelivery))}"
        }

        // Cancelled state
        if (currentStatus == "Cancelled") {
            iconStep1.text = "✗"
            iconStep1.setBackgroundColor(Color.parseColor("#C62828"))
        }
    }

    // ================================
    // ✅ Mark Step as Active (Green)
    // ================================
    private fun markActive(icon: TextView, mark: String) {
        icon.text = mark
        icon.setTextColor(Color.WHITE)
        icon.setBackgroundColor(Color.parseColor("#2E7D32"))
    }

    // ================================
    // ✅ Confirm Cancel Order
    // ================================
    private fun confirmCancelOrder() {
        AlertDialog.Builder(this)
            .setTitle("Cancel Order")
            .setMessage("Are you sure you want to cancel this order?")
            .setPositiveButton("Yes, Cancel") { _, _ ->
                val success = orderStorage.cancelOrder(currentOrder.orderId)
                if (success) {
                    Toast.makeText(this, "✅ Order cancelled", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "❌ Cannot cancel this order", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}