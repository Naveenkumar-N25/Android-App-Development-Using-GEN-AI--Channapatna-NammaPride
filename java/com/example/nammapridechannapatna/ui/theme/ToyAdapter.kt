package com.example.nammapridechannapatna

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ToyAdapter(
    private var toyList: List<Toy>,
    private val onAddToCart: (Toy) -> Unit,
    private val onViewDetails: (Toy) -> Unit
) : RecyclerView.Adapter<ToyAdapter.ToyViewHolder>() {

    class ToyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtToyImage: TextView = view.findViewById(R.id.txtToyImage)
        val txtToyName: TextView = view.findViewById(R.id.txtToyName)
        val txtGINumber: TextView = view.findViewById(R.id.txtGINumber)
        val txtToyPrice: TextView = view.findViewById(R.id.txtToyPrice)
        val btnViewDetails: Button = view.findViewById(R.id.btnViewDetails)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
        val rootLayout: LinearLayout = view as LinearLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_toy, parent, false)
        return ToyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToyViewHolder, position: Int) {
        val toy = toyList[position]

        holder.txtToyImage.text = toy.imageEmoji
        holder.txtToyName.text = toy.name

        // ✅ Show artisan badge for artisan-created toys
        if (toy.isArtisanCreated) {
            holder.txtGINumber.text = "🎨 ARTISAN | ${toy.giNumber}"
            holder.txtGINumber.setTextColor(Color.parseColor("#7B1FA2"))
        } else {
            holder.txtGINumber.text = "✓ ${toy.giNumber}"
            holder.txtGINumber.setTextColor(Color.parseColor("#2E7D32"))
        }

        holder.txtToyPrice.text = "₹${toy.price}"

        // ✅ Show out of stock state
        if (toy.stock <= 0) {
            holder.btnAddToCart.text = "OUT OF STOCK"
            holder.btnAddToCart.setBackgroundColor(Color.parseColor("#9E9E9E"))
            holder.btnAddToCart.isEnabled = false
        } else {
            holder.btnAddToCart.text = "Add to Cart"
            holder.btnAddToCart.setBackgroundColor(Color.parseColor("#8D4E2A"))
            holder.btnAddToCart.isEnabled = true
        }

        holder.btnViewDetails.setOnClickListener { onViewDetails(toy) }
        holder.btnAddToCart.setOnClickListener { onAddToCart(toy) }
    }

    override fun getItemCount(): Int = toyList.size

    fun updateList(newList: List<Toy>) {
        toyList = newList
        notifyDataSetChanged()
    }
}