package com.example.nammapridechannapatna

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EarningsActivity : ComponentActivity() {

    private lateinit var earningsStorage: EarningsStorage
    private lateinit var userStorage: UserStorage
    private lateinit var recyclerEarnings: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var txtEmptyMessage: TextView
    private lateinit var tabEarnings: TextView
    private lateinit var tabWithdrawals: TextView
    private var showingEarnings = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_earnings)
        overridePendingTransition(0, 0)

        earningsStorage = EarningsStorage(this)
        userStorage = UserStorage(this)

        val btnBack: TextView = findViewById(R.id.btnBack)
        recyclerEarnings = findViewById(R.id.recyclerEarnings)
        layoutEmpty = findViewById(R.id.layoutEmptyEarnings)
        txtEmptyMessage = findViewById(R.id.txtEmptyMessage)
        tabEarnings = findViewById(R.id.tabEarnings)
        tabWithdrawals = findViewById(R.id.tabWithdrawals)
        val btnWithdraw: Button = findViewById(R.id.btnWithdraw)

        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        recyclerEarnings.layoutManager = LinearLayoutManager(this)

        // Load stats
        loadStats()

        // Tab clicks
        tabEarnings.setOnClickListener {
            showingEarnings = true
            updateTabs()
            loadEarnings()
        }

        tabWithdrawals.setOnClickListener {
            showingEarnings = false
            updateTabs()
            loadWithdrawals()
        }

        // Withdraw click
        btnWithdraw.setOnClickListener {
            startActivity(Intent(this, WithdrawActivity::class.java))
            overridePendingTransition(0, 0)
        }

        // Default load earnings
        loadEarnings()
    }

    override fun onResume() {
        super.onResume()
        loadStats()
        if (showingEarnings) loadEarnings() else loadWithdrawals()
    }

    private fun loadStats() {
        val email = userStorage.getCurrentUserEmail()
        val txtBalance: TextView = findViewById(R.id.txtAvailableBalance)
        val txtTotal: TextView = findViewById(R.id.txtTotalEarnings)
        val txtWithdrawn: TextView = findViewById(R.id.txtTotalWithdrawn)
        val txtOrders: TextView = findViewById(R.id.txtTotalOrders)

        txtBalance.text = "₹${earningsStorage.getAvailableBalance(email)}"
        txtTotal.text = "₹${earningsStorage.getTotalEarnings(email)}"
        txtWithdrawn.text = "₹${earningsStorage.getTotalWithdrawn(email)}"
        txtOrders.text = "${earningsStorage.getTotalOrdersCount(email)}"
    }

    private fun updateTabs() {
        if (showingEarnings) {
            tabEarnings.setBackgroundColor(Color.parseColor("#5D4037"))
            tabEarnings.setTextColor(Color.WHITE)
            tabWithdrawals.setBackgroundColor(Color.parseColor("#FFE0B2"))
            tabWithdrawals.setTextColor(Color.parseColor("#5D4037"))
        } else {
            tabWithdrawals.setBackgroundColor(Color.parseColor("#5D4037"))
            tabWithdrawals.setTextColor(Color.WHITE)
            tabEarnings.setBackgroundColor(Color.parseColor("#FFE0B2"))
            tabEarnings.setTextColor(Color.parseColor("#5D4037"))
        }
    }

    private fun loadEarnings() {
        val email = userStorage.getCurrentUserEmail()
        val earnings = earningsStorage.getEarningsForArtisan(email)

        if (earnings.isEmpty()) {
            txtEmptyMessage.text = "No earnings yet"
            layoutEmpty.visibility = View.VISIBLE
            recyclerEarnings.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            recyclerEarnings.visibility = View.VISIBLE
            recyclerEarnings.adapter = EarningsAdapter(earnings)
        }
    }

    private fun loadWithdrawals() {
        val email = userStorage.getCurrentUserEmail()
        val withdrawals = earningsStorage.getWithdrawalsForArtisan(email)

        if (withdrawals.isEmpty()) {
            txtEmptyMessage.text = "No withdrawals yet"
            layoutEmpty.visibility = View.VISIBLE
            recyclerEarnings.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            recyclerEarnings.visibility = View.VISIBLE
            recyclerEarnings.adapter = WithdrawalsAdapter(withdrawals)
        }
    }

    // ================================
    // ✅ Earnings Adapter
    // ================================
    inner class EarningsAdapter(
        private val list: List<Earning>
    ) : RecyclerView.Adapter<EarningsAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val txtToyName: TextView = view.findViewById(R.id.txtEarningToyName)
            val txtGiId: TextView = view.findViewById(R.id.txtEarningGiId)
            val txtOrderId: TextView = view.findViewById(R.id.txtEarningOrderId)
            val txtAmount: TextView = view.findViewById(R.id.txtEarningAmount)
            val txtStatus: TextView = view.findViewById(R.id.txtEarningStatus)
            val txtDate: TextView = view.findViewById(R.id.txtEarningDate)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_earning, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val earning = list[position]
            holder.txtToyName.text = "🪆 ${earning.toyName}"
            holder.txtGiId.text = "✓ ${earning.toyGiId}"
            holder.txtOrderId.text = "📦 Order: ${earning.orderId} | Vendor: ${earning.vendorName}"
            holder.txtAmount.text = "+₹${earning.amount}"

            val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(Date(earning.timestamp))
            holder.txtDate.text = "🕐 $date"
        }

        override fun getItemCount(): Int = list.size
    }

    // ================================
    // ✅ Withdrawals Adapter
    // ================================
    inner class WithdrawalsAdapter(
        private val list: List<Withdrawal>
    ) : RecyclerView.Adapter<WithdrawalsAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val txtId: TextView = view.findViewById(R.id.txtWithdrawalId)
            val txtBank: TextView = view.findViewById(R.id.txtWithdrawalBank)
            val txtAccount: TextView = view.findViewById(R.id.txtWithdrawalAccount)
            val txtAmount: TextView = view.findViewById(R.id.txtWithdrawalAmount)
            val txtStatus: TextView = view.findViewById(R.id.txtWithdrawalStatus)
            val txtDate: TextView = view.findViewById(R.id.txtWithdrawalDate)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_withdrawal, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val w = list[position]
            holder.txtId.text = "💸 ${w.withdrawalId}"
            holder.txtBank.text = "🏦 ${w.bankName}"
            holder.txtAccount.text = "💳 ****${w.accountNumber.takeLast(4)}"
            holder.txtAmount.text = "-₹${w.amount}"
            holder.txtStatus.text = "✓ ${w.status}"

            val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(Date(w.timestamp))
            holder.txtDate.text = "🕐 $date"
        }

        override fun getItemCount(): Int = list.size
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }
}