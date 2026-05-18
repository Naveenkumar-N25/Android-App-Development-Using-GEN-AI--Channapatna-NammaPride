package com.example.nammapridechannapatna

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private var cartList: List<Toy>,
    private val onRemove: (Toy) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCartImage: TextView = view.findViewById(R.id.txtCartImage)
        val txtCartName: TextView = view.findViewById(R.id.txtCartName)
        val txtCartGI: TextView = view.findViewById(R.id.txtCartGI)
        val txtCartPrice: TextView = view.findViewById(R.id.txtCartPrice)
        val btnRemove: TextView = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val toy = cartList[position]
        holder.txtCartImage.text = toy.imageEmoji
        holder.txtCartName.text = toy.name
        holder.txtCartGI.text = toy.giNumber
        holder.txtCartPrice.text = "₹${toy.price}"

        holder.btnRemove.setOnClickListener { onRemove(toy) }
    }

    override fun getItemCount(): Int = cartList.size

    fun updateList(newList: List<Toy>) {
        cartList = newList
        notifyDataSetChanged()
    }
}