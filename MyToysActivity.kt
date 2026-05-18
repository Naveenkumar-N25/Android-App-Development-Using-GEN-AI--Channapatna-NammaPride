package com.example.nammapridechannapatna

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyToysActivity : ComponentActivity() {

    private lateinit var artisanStorage: ArtisanStorage
    private lateinit var userStorage: UserStorage
    private lateinit var recyclerMyToys: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var txtCount: TextView
    private lateinit var adapter: MyToysAdapter

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
        setContentView(R.layout.activity_my_toys)
        overridePendingTransition(0, 0)

        artisanStorage = ArtisanStorage(this)
        userStorage = UserStorage(this)

        val btnBack: TextView = findViewById(R.id.btnBack)
        recyclerMyToys = findViewById(R.id.recyclerMyToys)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        txtCount = findViewById(R.id.txtMyToysCount)

        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        adapter = MyToysAdapter(emptyList())
        recyclerMyToys.layoutManager = LinearLayoutManager(this)
        recyclerMyToys.adapter = adapter

        loadToys()
    }

    override fun onResume() {
        super.onResume()
        loadToys()
    }

    private fun loadToys() {
        val email = userStorage.getCurrentUserEmail()
        val toys = artisanStorage.getMyToys(email)
        adapter.updateList(toys)
        txtCount.text = "${toys.size} toys created"

        if (toys.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            recyclerMyToys.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            recyclerMyToys.visibility = View.VISIBLE
        }
    }

    inner class MyToysAdapter(
        private var toys: List<ArtisanToy>
    ) : RecyclerView.Adapter<MyToysAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val container: LinearLayout = view as LinearLayout
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val container = LinearLayout(parent.context)
            container.orientation = LinearLayout.VERTICAL
            container.setBackgroundColor(Color.WHITE)
            container.setPadding(20, 16, 20, 16)

            val params = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 6, 8, 6)
            container.layoutParams = params

            return VH(container)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val toy = toys[position]
            holder.container.removeAllViews()

            // Top row with image and info
            val topRow = LinearLayout(this@MyToysActivity)
            topRow.orientation = LinearLayout.HORIZONTAL

            val img = ImageView(this@MyToysActivity)
            img.setImageResource(photoResources[toy.photoIndex.coerceIn(0, 5)])
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            val imgParams = LinearLayout.LayoutParams(150, 150)
            img.layoutParams = imgParams
            topRow.addView(img)

            val infoLayout = LinearLayout(this@MyToysActivity)
            infoLayout.orientation = LinearLayout.VERTICAL
            val infoParams = LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            infoParams.setMargins(20, 0, 0, 0)
            infoLayout.layoutParams = infoParams

            val nameView = TextView(this@MyToysActivity)
            nameView.text = toy.name
            nameView.textSize = 16f
            nameView.setTextColor(Color.parseColor("#3E2723"))
            nameView.setTypeface(null, android.graphics.Typeface.BOLD)
            infoLayout.addView(nameView)

            val giView = TextView(this@MyToysActivity)
            giView.text = "✓ ${toy.giId}"
            giView.textSize = 11f
            giView.setTextColor(Color.parseColor("#2E7D32"))
            giView.setTypeface(null, android.graphics.Typeface.BOLD)
            infoLayout.addView(giView)

            val priceView = TextView(this@MyToysActivity)
            priceView.text = "₹${toy.price}"
            priceView.textSize = 18f
            priceView.setTextColor(Color.parseColor("#C62828"))
            priceView.setTypeface(null, android.graphics.Typeface.BOLD)
            val pParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            pParams.topMargin = 4
            priceView.layoutParams = pParams
            infoLayout.addView(priceView)

            val stockView = TextView(this@MyToysActivity)
            stockView.text = if (toy.stock > 0) "📦 Stock: ${toy.stock}" else "⚠️ Out of stock"
            stockView.textSize = 12f
            stockView.setTextColor(if (toy.stock > 0)
                Color.parseColor("#2E7D32") else Color.parseColor("#C62828"))
            infoLayout.addView(stockView)

            topRow.addView(infoLayout)
            holder.container.addView(topRow)

            // Materials
            val matView = TextView(this@MyToysActivity)
            matView.text = "🌿 ${toy.materials}"
            matView.textSize = 11f
            matView.setTextColor(Color.parseColor("#5D4037"))
            val mParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            mParams.topMargin = 12
            matView.layoutParams = mParams
            holder.container.addView(matView)

            // Date
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val dateView = TextView(this@MyToysActivity)
            dateView.text = "🕐 Created: ${dateFormat.format(Date(toy.createdDate))}"
            dateView.textSize = 11f
            dateView.setTextColor(Color.parseColor("#8D6E63"))
            val dParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dParams.topMargin = 4
            dateView.layoutParams = dParams
            holder.container.addView(dateView)

            // Click to view full details
            holder.container.setOnClickListener {
                showToyDetails(toy)
            }

            holder.container.setOnLongClickListener {
                confirmDelete(toy)
                true
            }
        }

        override fun getItemCount(): Int = toys.size

        fun updateList(newList: List<ArtisanToy>) {
            toys = newList
            notifyDataSetChanged()
        }
    }

    private fun showToyDetails(toy: ArtisanToy) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        AlertDialog.Builder(this)
            .setTitle("🪆 ${toy.name}")
            .setMessage(
                "✓ GI ID: ${toy.giId}\n\n" +
                        "💰 Price: ₹${toy.price}\n" +
                        "📦 Stock: ${toy.stock} pieces\n\n" +
                        "🌿 Materials:\n${toy.materials}\n\n" +
                        "📖 Story:\n${toy.craftingStory}\n\n" +
                        "🕐 Created: ${dateFormat.format(Date(toy.createdDate))}"
            )
            .setPositiveButton("OK", null)
            .setNegativeButton("Delete") { _, _ ->
                confirmDelete(toy)
            }
            .show()
    }

    private fun confirmDelete(toy: ArtisanToy) {
        AlertDialog.Builder(this)
            .setTitle("Delete Toy")
            .setMessage("Delete ${toy.name}?")
            .setPositiveButton("Yes") { _, _ ->
                if (artisanStorage.deleteToy(toy.giId)) {
                    Toast.makeText(this, "✅ Deleted", Toast.LENGTH_SHORT).show()
                    loadToys()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }
}