package com.example.nammapridechannapatna

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.drawerlayout.widget.DrawerLayout

class ArtisanDashboardActivity : ComponentActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userStorage: UserStorage
    private lateinit var artisanStorage: ArtisanStorage
    private lateinit var earningsStorage: EarningsStorage

    private lateinit var txtArtisanName: TextView
    private lateinit var statTotalToys: TextView
    private lateinit var statTotalStock: TextView
    private lateinit var statOutOfStock: TextView
    private lateinit var txtBalanceHeader: TextView
    private lateinit var txtEarningsAmount: TextView
    private lateinit var drawerEarningsBadge: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artisan_dashboard)
        overridePendingTransition(0, 0)

        userStorage = UserStorage(this)
        artisanStorage = ArtisanStorage(this)
        earningsStorage = EarningsStorage(this)

        drawerLayout = findViewById(R.id.drawerLayout)
        txtArtisanName = findViewById(R.id.txtArtisanName)
        statTotalToys = findViewById(R.id.statTotalToys)
        statTotalStock = findViewById(R.id.statTotalStock)
        statOutOfStock = findViewById(R.id.statOutOfStock)
        txtBalanceHeader = findViewById(R.id.txtBalanceHeader)
        txtEarningsAmount = findViewById(R.id.txtEarningsAmount)
        drawerEarningsBadge = findViewById(R.id.drawerEarningsBadge)

        val drawerArtisanName: TextView = findViewById(R.id.drawerArtisanName)
        val drawerArtisanEmail: TextView = findViewById(R.id.drawerArtisanEmail)

        val name = userStorage.getCurrentUserName()
        val email = userStorage.getCurrentUserEmail()

        txtArtisanName.text = "Hello, $name!"
        drawerArtisanName.text = name
        drawerArtisanEmail.text = email

        // Profile Icon → Open Drawer
        findViewById<TextView>(R.id.btnArtisanProfile).setOnClickListener {
            drawerLayout.openDrawer(Gravity.END)
        }

        // ✅ Card Click Listeners
        findViewById<LinearLayout>(R.id.cardAddToy).setOnClickListener {
            navigateInstant(AddToyActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.cardMyToys).setOnClickListener {
            navigateInstant(MyToysActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.cardInventory).setOnClickListener {
            navigateInstant(InventoryActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.cardProfile).setOnClickListener {
            navigateInstant(ArtisanProfileActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.cardHeritage).setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("🏺 Heritage Process Logger")
                .setMessage("Use 'Add Toy' option to log:\n\n" +
                        "• Materials Used\n  (Aale Mara wood, Vegetable dyes)\n\n" +
                        "• Crafting Story\n  (Heritage process & techniques)\n\n" +
                        "All your toys preserve the heritage!")
                .setPositiveButton("Add Toy Now") { _, _ ->
                    navigateInstant(AddToyActivity::class.java)
                }
                .setNegativeButton("Close", null)
                .show()
        }

        // ✅ NEW - Earnings Card
        findViewById<LinearLayout>(R.id.cardEarnings).setOnClickListener {
            navigateInstant(EarningsActivity::class.java)
        }

        // ✅ NEW - Bank Card
        findViewById<LinearLayout>(R.id.cardBank).setOnClickListener {
            navigateInstant(BankDetailsActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.cardLogout).setOnClickListener {
            confirmLogout()
        }

        // Drawer items
        findViewById<LinearLayout>(R.id.drawerMenuProfile).setOnClickListener {
            drawerLayout.closeDrawer(Gravity.END)
            navigateInstant(ArtisanProfileActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.drawerMenuToys).setOnClickListener {
            drawerLayout.closeDrawer(Gravity.END)
            navigateInstant(MyToysActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.drawerMenuInventory).setOnClickListener {
            drawerLayout.closeDrawer(Gravity.END)
            navigateInstant(InventoryActivity::class.java)
        }

        // ✅ NEW - Drawer Earnings
        findViewById<LinearLayout>(R.id.drawerMenuEarnings).setOnClickListener {
            drawerLayout.closeDrawer(Gravity.END)
            navigateInstant(EarningsActivity::class.java)
        }

        // ✅ NEW - Drawer Bank
        findViewById<LinearLayout>(R.id.drawerMenuBank).setOnClickListener {
            drawerLayout.closeDrawer(Gravity.END)
            navigateInstant(BankDetailsActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.drawerMenuLogout).setOnClickListener {
            drawerLayout.closeDrawer(Gravity.END)
            confirmLogout()
        }
    }

    override fun onResume() {
        super.onResume()
        updateStats()
        updateEarningsDisplay()
    }

    private fun updateStats() {
        val email = userStorage.getCurrentUserEmail()
        statTotalToys.text = artisanStorage.getTotalToys(email).toString()
        statTotalStock.text = artisanStorage.getTotalStock(email).toString()
        statOutOfStock.text = artisanStorage.getOutOfStockCount(email).toString()
    }

    // ================================
    // ✅ Update Earnings Display
    // ================================
    private fun updateEarningsDisplay() {
        val email = userStorage.getCurrentUserEmail()
        val balance = earningsStorage.getAvailableBalance(email)

        txtBalanceHeader.text = "₹$balance"
        txtEarningsAmount.text = "₹$balance"
        drawerEarningsBadge.text = "₹$balance"
    }

    // ✅ INSTANT NAVIGATION
    private fun navigateInstant(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun confirmLogout() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Exit your workshop?")
            .setPositiveButton("Yes") { _, _ ->
                userStorage.logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END)
        } else {
            super.onBackPressed()
            overridePendingTransition(0, 0)
        }
    }
}