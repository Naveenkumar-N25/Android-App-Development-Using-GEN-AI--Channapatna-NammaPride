package com.example.nammapridechannapatna

data class Order(
    val orderId: String,
    val items: String,           // Toy IDs comma separated
    val itemNames: String,       // Toy names
    val totalAmount: Int,
    val orderDate: Long,
    val status: String,          // Confirmed, Packed, Shipped, OutForDelivery, Delivered
    val deliveryAddress: String,
    val customerName: String,
    val customerPhone: String,
    val expectedDelivery: Long
)