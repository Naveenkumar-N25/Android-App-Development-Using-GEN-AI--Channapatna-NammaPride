package com.example.nammapridechannapatna

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VendorDashboardActivity : ComponentActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var txtVendorName: TextView
    private lateinit var searchToys: EditText
    private lateinit var recyclerToys: RecyclerView
    private lateinit var btnCart: TextView
    private lateinit var txtCartBadge: TextView
    private lateinit var btnProfile: TextView
    private lateinit var txtSectionTitle: TextView

    // Drawer Views
    private lateinit var drawerUserName: TextView
    private lateinit var drawerUserEmail: TextView
    private lateinit var drawerUserType: TextView
    private lateinit var menuProfile: LinearLayout
    private lateinit var menuPassword: LinearLayout
    private lateinit var menuOrders: LinearLayout
    private lateinit var menuCart: LinearLayout
    private lateinit var menuCartBadge: TextView
    private lateinit var menuAbout: LinearLayout
    private lateinit var menuLogout: LinearLayout

    private lateinit var toyStorage: ToyStorage
    private lateinit var userStorage: UserStorage
    private lateinit var toyAdapter: ToyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendor_dashboard)
        overridePendingTransition(0, 0)

        toyStorage = ToyStorage(this)
        userStorage = UserStorage(this)

        // Main Views
        drawerLayout = findViewById(R.id.drawerLayout)
        txtVendorName = findViewById(R.id.txtVendorName)
        searchToys = findViewById(R.id.searchToys)
        recyclerToys = findViewById(R.id.recyclerToys)
        btnCart = findViewById(R.id.btnCart)
        txtCartBadge = findViewById(R.id.txtCartBadge)
        btnProfile = findViewById(R.id.btnProfile)
        txtSectionTitle = findViewById(R.id.txtSectionTitle)

        // Drawer Views
        drawerUserName = findViewById(R.id.drawerUserName)
        drawerUserEmail = findViewById(R.id.drawerUserEmail)
        drawerUserType = findViewById(R.id.drawerUserType)
        menuProfile = findViewById(R.id.menuProfile)
        menuPassword = findViewById(R.id.menuPassword)
        menuOrders = findViewById(R.id.menuOrders)
        menuCart = findViewById(R.id.menuCart)
        menuCartBadge = findViewById(R.id.menuCartBadge)
        menuAbout = findViewById(R.id.menuAbout)
        menuLogout = findViewById(R.id.menuLogout)

        txtVendorName.text = "Hello, ${userStorage.getCurrentUserName()}!"

        setupRecyclerView()
        setupSearch()
        setupDrawer()
        updateCartBadge()

        btnProfile.setOnClickListener {
            updateDrawerInfo()
            drawerLayout.openDrawer(Gravity.END)
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }

    // ✅ CRITICAL - Reload toys when returning to dashboard
    override fun onResume() {
        super.onResume()
        refreshToyList()
        updateCartBadge()
        txtVendorName.text = "Hello, ${userStorage.getCurrentUserName()}!"
        updateDrawerInfo()
    }

    // ================================
    // ✅ Refresh Toy List from Storage
    // ================================
    private fun refreshToyList() {
        val allToys = toyStorage.getAllToys()
        toyAdapter.updateList(allToys)

        // Show count of artisan toys
        val artisanCount = toyStorage.getArtisanToyCount()
        val totalCount = allToys.size
        txtSectionTitle.text = "🎨 $totalCount Authentic Toys ($artisanCount from artisans)"
    }

    private fun setupDrawer() {
        updateDrawerInfo()

        menuProfile.setOnClickListener {
            drawerLayout.closeDrawer(Gravity.END)
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(0, 0)
        }

        menuPassword.setOnClickListener {
            drawerLayout.closeDrawer(Gravity.END)
            startActivity(Intent(this, ChangePasswordActivity::class.java))
            overridePendingTransition(0, 0)
        }

        menuOrders.setOnClickListener {
            drawerLayout.closeDrawer(Gravity.END)
            startActivity(Intent(this, OrderActivity::class.java))
            overridePendingTransition(0, 0)
        }

        menuCart.setOnClickListener {
            drawerLayout.closeDrawer(Gravity.END)
            startActivity(Intent(this, CartActivity::class.java))
            overridePendingTransition(0, 0)
        }

        menuAbout.setOnClickListener {
            drawerLayout.closeDrawer(Gravity.END)
            showAboutDialog()
        }

        menuLogout.setOnClickListener {
            drawerLayout.closeDrawer(Gravity.END)
            confirmLogout()
        }
    }

    private fun updateDrawerInfo() {
        drawerUserName.text = userStorage.getCurrentUserName()
        drawerUserEmail.text = userStorage.getCurrentUserEmail()
        drawerUserType.text = userStorage.getCurrentUserType().uppercase()
        menuCartBadge.text = toyStorage.getCartCount().toString()
    }

    private fun confirmLogout() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
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

    private fun showAboutDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle("ℹ️ About Channapatna Namma")
            .setMessage(
                "Channapatna Namma v1.0\n\n" +
                        "🎨 An app to promote authentic\n" +
                        "GI-certified Channapatna toys\n" +
                        "and support local artisans.\n\n" +
                        "🪆 Made with ❤️ in Karnataka\n\n" +
                        "© 2026 Channapatna Pride"
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun setupRecyclerView() {
        toyAdapter = ToyAdapter(
            toyStorage.getAllToys(),
            onAddToCart = { toy -> handleAddToCart(toy) },
            onViewDetails = { toy -> openToyDetails(toy) }
        )
        recyclerToys.layoutManager = LinearLayoutManager(this)
        recyclerToys.adapter = toyAdapter
    }

    private fun setupSearch() {
        searchToys.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val results = toyStorage.searchToys(query)
                toyAdapter.updateList(results)
                txtSectionTitle.text = if (query.isEmpty()) {
                    val artisanCount = toyStorage.getArtisanToyCount()
                    val totalCount = toyStorage.getTotalToyCount()
                    "🎨 $totalCount Authentic Toys ($artisanCount from artisans)"
                } else {
                    "🔍 ${results.size} result(s) for \"$query\""
                }
            }
        })
    }

    private fun handleAddToCart(toy: Toy) {
        // Check stock
        if (toy.stock <= 0) {
            Toast.makeText(this, "❌ ${toy.name} is out of stock!", Toast.LENGTH_SHORT).show()
            return
        }

        val success = toyStorage.addToCart(toy.id)
        if (success) {
            val from = if (toy.isArtisanCreated) "🎨" else "🪆"
            Toast.makeText(this, "✅ $from ${toy.name} added!", Toast.LENGTH_SHORT).show()
            updateCartBadge()
            updateDrawerInfo()
        } else {
            Toast.makeText(this, "⚠️ Already in cart", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openToyDetails(toy: Toy) {
        val intent = Intent(this, ToyDetailActivity::class.java)
        intent.putExtra("TOY_ID", toy.id)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun updateCartBadge() {
        val count = toyStorage.getCartCount()
        txtCartBadge.text = count.toString()
        txtCartBadge.visibility = if (count == 0) TextView.GONE else TextView.VISIBLE
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