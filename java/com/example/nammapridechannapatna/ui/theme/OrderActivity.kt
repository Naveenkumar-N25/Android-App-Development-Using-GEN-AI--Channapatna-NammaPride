package com.example.nammapridechannapatna

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderActivity : ComponentActivity() {

    private lateinit var orderStorage: OrderStorage
    private lateinit var recyclerOrders: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var txtOrderCount: TextView
    private lateinit var btnBack: TextView
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        orderStorage = OrderStorage(this)

        btnBack = findViewById(R.id.btnBack)
        recyclerOrders = findViewById(R.id.recyclerOrders)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        txtOrderCount = findViewById(R.id.txtOrderCount)

        btnBack.setOnClickListener { finish() }

        setupRecyclerView()
        loadOrders()
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            orderStorage.getAllOrders(),
            orderStorage,
            onTrackClick = { order ->
                val intent = Intent(this, OrderTrackingActivity::class.java)
                intent.putExtra("ORDER_ID", order.orderId)
                startActivity(intent)
            }
        )
        recyclerOrders.layoutManager = LinearLayoutManager(this)
        recyclerOrders.adapter = orderAdapter
    }

    private fun loadOrders() {
        val orders = orderStorage.getAllOrders()
        orderAdapter.updateList(orders)
        txtOrderCount.text = "${orders.size} orders"

        if (orders.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            recyclerOrders.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            recyclerOrders.visibility = View.VISIBLE
        }
    }
}