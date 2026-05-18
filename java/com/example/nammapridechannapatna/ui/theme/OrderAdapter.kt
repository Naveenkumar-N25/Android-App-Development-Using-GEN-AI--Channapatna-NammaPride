package com.example.nammapridechannapatna

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(
    private var orderList: List<Order>,
    private val orderStorage: OrderStorage,
    private val onTrackClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtOrderId: TextView = view.findViewById(R.id.txtOrderId)
        val txtOrderStatus: TextView = view.findViewById(R.id.txtOrderStatus)
        val txtOrderItems: TextView = view.findViewById(R.id.txtOrderItems)
        val txtOrderDate: TextView = view.findViewById(R.id.txtOrderDate)
        val txtOrderAmount: TextView = view.findViewById(R.id.txtOrderAmount)
        val btnTrackOrder: Button = view.findViewById(R.id.btnTrackOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        val currentStatus = orderStorage.getCurrentStatus(order)

        holder.txtOrderId.text = "Order: ${order.orderId}"
        holder.txtOrderItems.text = "📦 ${order.itemNames}"
        holder.txtOrderAmount.text = "₹${order.totalAmount}"

        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            .format(Date(order.orderDate))
        holder.txtOrderDate.text = "🕐 $dateStr"

        // Status colors
        when (currentStatus) {
            "Confirmed" -> {
                holder.txtOrderStatus.text = "✓ Confirmed"
                holder.txtOrderStatus.setBackgroundColor(Color.parseColor("#2E7D32"))
            }
            "Packed" -> {
                holder.txtOrderStatus.text = "📦 Packed"
                holder.txtOrderStatus.setBackgroundColor(Color.parseColor("#1976D2"))
            }
            "Shipped" -> {
                holder.txtOrderStatus.text = "🚚 Shipped"
                holder.txtOrderStatus.setBackgroundColor(Color.parseColor("#7B1FA2"))
            }
            "OutForDelivery" -> {
                holder.txtOrderStatus.text = "🛵 Out for Delivery"
                holder.txtOrderStatus.setBackgroundColor(Color.parseColor("#F57C00"))
            }
            "Delivered" -> {
                holder.txtOrderStatus.text = "✅ Delivered"
                holder.txtOrderStatus.setBackgroundColor(Color.parseColor("#388E3C"))
            }
            "Cancelled" -> {
                holder.txtOrderStatus.text = "❌ Cancelled"
                holder.txtOrderStatus.setBackgroundColor(Color.parseColor("#C62828"))
            }
        }

        holder.btnTrackOrder.setOnClickListener { onTrackClick(order) }
    }

    override fun getItemCount(): Int = orderList.size

    fun updateList(newList: List<Order>) {
        orderList = newList
        notifyDataSetChanged()
    }
}