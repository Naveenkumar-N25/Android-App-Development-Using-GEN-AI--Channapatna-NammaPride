package com.example.nammapridechannapatna

import android.content.Context
import android.content.SharedPreferences

class OrderStorage(context: Context) {

    private val context: Context = context
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ChannapatnaOrders", Context.MODE_PRIVATE)

    // ================================
    // ✅ PLACE NEW ORDER (with credit to artisans)
    // ================================
    fun placeOrder(
        items: String,
        itemNames: String,
        totalAmount: Int,
        deliveryAddress: String,
        customerName: String,
        customerPhone: String
    ): String {
        val orderId = "ORD${System.currentTimeMillis().toString().takeLast(8)}"
        val orderDate = System.currentTimeMillis()
        val expectedDelivery = orderDate + (5 * 24 * 60 * 60 * 1000L)

        val newOrder = "$orderId|$items|$itemNames|$totalAmount|$orderDate|" +
                "Confirmed|$deliveryAddress|$customerName|$customerPhone|$expectedDelivery;"

        val allOrders = prefs.getString("all_orders", "") ?: ""
        prefs.edit().putString("all_orders", allOrders + newOrder).commit()

        // ✅ Credit money to artisans for their toys
        creditArtisansForOrder(items, orderId, customerName)

        return orderId
    }

    // ================================
    // ✅ CREDIT ARTISANS FOR ORDER
    // ================================
    private fun creditArtisansForOrder(itemIds: String, orderId: String, customerName: String) {
        val toyStorage = ToyStorage(context)
        val artisanStorage = ArtisanStorage(context)
        val earningsStorage = EarningsStorage(context)

        val toyIds = itemIds.split(",").filter { it.isNotEmpty() }

        for (toyId in toyIds) {
            val toy = toyStorage.getToyById(toyId)

            // Only credit if it's an artisan-created toy
            if (toy != null && toy.isArtisanCreated && toy.artisanEmail.isNotEmpty()) {
                // Credit money to artisan
                earningsStorage.creditEarning(
                    artisanEmail = toy.artisanEmail,
                    toyGiId = toy.id,
                    toyName = toy.name,
                    amount = toy.price,
                    orderId = orderId,
                    vendorName = customerName
                )

                // Decrease stock
                val artisanToy = artisanStorage.getToyByGI(toy.id)
                if (artisanToy != null && artisanToy.stock > 0) {
                    artisanStorage.updateStock(toy.id, artisanToy.stock - 1)
                }
            }
        }
    }

    fun getAllOrders(): List<Order> {
        val data = prefs.getString("all_orders", "") ?: ""
        if (data.isEmpty()) return emptyList()

        return data.split(";").mapNotNull { entry ->
            val cleaned = entry.trim()
            if (cleaned.isEmpty()) return@mapNotNull null

            val parts = cleaned.split("|")
            if (parts.size >= 10) {
                Order(
                    orderId = parts[0],
                    items = parts[1],
                    itemNames = parts[2],
                    totalAmount = parts[3].toIntOrNull() ?: 0,
                    orderDate = parts[4].toLongOrNull() ?: 0L,
                    status = parts[5],
                    deliveryAddress = parts[6],
                    customerName = parts[7],
                    customerPhone = parts[8],
                    expectedDelivery = parts[9].toLongOrNull() ?: 0L
                )
            } else null
        }.reversed()
    }

    fun getOrderById(orderId: String): Order? {
        return getAllOrders().find { it.orderId == orderId }
    }

    fun getCurrentStatus(order: Order): String {
        val now = System.currentTimeMillis()
        val daysSinceOrder = ((now - order.orderDate) / (24 * 60 * 60 * 1000)).toInt()

        return when {
            daysSinceOrder >= 5 -> "Delivered"
            daysSinceOrder >= 4 -> "OutForDelivery"
            daysSinceOrder >= 2 -> "Shipped"
            daysSinceOrder >= 1 -> "Packed"
            else -> "Confirmed"
        }
    }

    fun getOrderCount(): Int = getAllOrders().size

    fun cancelOrder(orderId: String): Boolean {
        val data = prefs.getString("all_orders", "") ?: ""
        if (data.isEmpty()) return false

        val orders = data.split(";").filter { it.isNotEmpty() }.toMutableList()
        var updated = false

        for (i in orders.indices) {
            val parts = orders[i].split("|")
            if (parts.size >= 10 && parts[0] == orderId) {
                if (parts[5] == "Confirmed" || parts[5] == "Packed") {
                    orders[i] = parts.toMutableList().apply { this[5] = "Cancelled" }.joinToString("|")
                    updated = true
                }
                break
            }
        }

        if (updated) {
            prefs.edit().putString("all_orders", orders.joinToString(";") + ";").commit()
        }
        return updated
    }
}