package com.example.nammapridechannapatna

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InventoryActivity : ComponentActivity() {

    private lateinit var artisanStorage: ArtisanStorage
    private lateinit var userStorage: UserStorage
    private lateinit var recyclerInventory: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var txtStats: TextView
    private lateinit var adapter: InventoryAdapter

    private val photoResources = listOf(
        R.drawable.toy_image_1,
        R.drawable.toy_image_2,
        R.drawable.toy_image_3,
        R.drawable.toy_image_4,
        R.drawable.toy_image_5,
        R.drawable.toy_image_6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        overridePendingTransition(0, 0)

        artisanStorage = ArtisanStorage(this)
        userStorage = UserStorage(this)

        val btnBack: TextView = findViewById(R.id.btnBack)
        recyclerInventory = findViewById(R.id.recyclerInventory)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        txtStats = findViewById(R.id.txtInventoryStats)

        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        adapter = InventoryAdapter(emptyList())
        recyclerInventory.layoutManager = LinearLayoutManager(this)
        recyclerInventory.adapter = adapter

        loadInventory()
    }

    override fun onResume() {
        super.onResume()
        loadInventory()
    }

    private fun loadInventory() {
        val email = userStorage.getCurrentUserEmail()
        val toys = artisanStorage.getMyToys(email)
        adapter.updateList(toys)

        val totalStock = toys.sumOf { it.stock }
        txtStats.text = "${toys.size} items | $totalStock total stock"

        if (toys.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            recyclerInventory.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            recyclerInventory.visibility = View.VISIBLE
        }
    }

    inner class InventoryAdapter(
        private var toys: List<ArtisanToy>
    ) : RecyclerView.Adapter<InventoryAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val img: ImageView = view.findViewById(R.id.imgInventoryToy)
            val name: TextView = view.findViewById(R.id.txtInventoryName)
            val gi: TextView = view.findViewById(R.id.txtInventoryGI)
            val stock: TextView = view.findViewById(R.id.txtInventoryStock)
            val btnMinus: TextView = view.findViewById(R.id.btnMinus)
            val txtCurrentStock: TextView = view.findViewById(R.id.txtCurrentStock)
            val btnPlus: TextView = view.findViewById(R.id.btnPlus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_inventory, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val toy = toys[position]
            val photoIndex = toy.photoIndex.coerceIn(0, photoResources.size - 1)
            holder.img.setImageResource(photoResources[photoIndex])
            holder.name.text = toy.name
            holder.gi.text = "✓ ${toy.giId}"
            holder.stock.text = "Price: ₹${toy.price}"
            holder.txtCurrentStock.text = toy.stock.toString()

            holder.btnPlus.setOnClickListener {
                val newStock = toy.stock + 1
                if (artisanStorage.updateStock(toy.giId, newStock)) {
                    holder.txtCurrentStock.text = newStock.toString()
                    Toast.makeText(this@InventoryActivity,
                        "+ Stock: $newStock | Updated for vendors!",
                        Toast.LENGTH_SHORT).show()
                    loadInventory()
                }
            }

            holder.btnMinus.setOnClickListener {
                if (toy.stock > 0) {
                    val newStock = toy.stock - 1
                    if (artisanStorage.updateStock(toy.giId, newStock)) {
                        holder.txtCurrentStock.text = newStock.toString()
                        Toast.makeText(this@InventoryActivity,
                            "- Stock: $newStock | Updated for vendors!",
                            Toast.LENGTH_SHORT).show()
                        loadInventory()
                    }
                } else {
                    Toast.makeText(this@InventoryActivity,
                        "⚠️ Stock is 0",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun getItemCount(): Int = toys.size

        fun updateList(newList: List<ArtisanToy>) {
            toys = newList
            notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }
}